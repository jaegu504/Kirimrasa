package com.wabulk.sender.engine.baileys

import com.wabulk.sender.engine.PingResult
import com.wabulk.sender.engine.SendResult
import com.wabulk.sender.engine.SimulatedWhatsAppEngine
import com.wabulk.sender.engine.WhatsAppEngine
import com.wabulk.sender.engine.model.NodeSession
import com.wabulk.sender.engine.model.NodeStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.IOException
import java.util.concurrent.TimeUnit

@Serializable
internal data class BaileysPairRequest(
    val phoneNumber: String
)

@Serializable
internal data class BaileysSendRequest(
    val recipient: String,
    val message: String,
    val media: List<String> = emptyList()
)

@Serializable
internal data class BaileysPresenceRequest(
    val recipient: String,
    val durationMs: Long = 3000L
)

class BaileysBridgeClient(
    override val node: NodeSession,
    private val configProvider: () -> BaileysConfig = { BaileysConfigManager.config.value },
    private val httpClient: OkHttpClient = defaultClient,
    private val fallbackEngine: WhatsAppEngine = SimulatedWhatsAppEngine(node),
    val fallbackOnException: Boolean = false,
    private val onStatusChanged: ((NodeStatus) -> Unit)? = null
) : WhatsAppEngine {

    var context: android.content.Context? = null

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val _connectionState = MutableStateFlow(node.status)
    val connectionState: StateFlow<NodeStatus> = _connectionState.asStateFlow()

    private var activeWebSocket: WebSocket? = null

    override suspend fun initialize() {
        if (!configProvider().isEnabled) {
            fallbackEngine.initialize()
            return
        }
        connectWebSocket()
    }

    override suspend fun requestPairingCode(phoneNumber: String): String = withContext(Dispatchers.IO) {
        val config = configProvider()
        if (!config.isEnabled) {
            return@withContext fallbackEngine.requestPairingCode(phoneNumber)
        }

        try {
            val url = buildUrl("/api/nodes/${node.id}/pair")
            val payload = json.encodeToString(BaileysPairRequest(phoneNumber = phoneNumber))
            val request = Request.Builder()
                .url(url)
                .post(payload.toRequestBody(jsonMediaType))
                .withAuth(config)
                .build()

            httpClient.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val code = extractPairingCode(body)
                    if (!code.isNullOrBlank()) {
                        return@withContext code
                    }
                }

                if (fallbackOnException) {
                    return@withContext fallbackEngine.requestPairingCode(phoneNumber)
                } else {
                    throw IOException("Failed to obtain pairing code: HTTP ${response.code} $body")
                }
            }
        } catch (e: Exception) {
            if (fallbackOnException) {
                return@withContext fallbackEngine.requestPairingCode(phoneNumber)
            } else {
                throw e
            }
        }
    }

    override suspend fun sendMessage(
        recipientPhone: String,
        messageText: String,
        mediaUrls: List<String>
    ): SendResult = withContext(Dispatchers.IO) {
        val config = configProvider()
        if (!config.isEnabled) {
            return@withContext fallbackEngine.sendMessage(recipientPhone, messageText, mediaUrls)
        }

        try {
            val preparedMedia = prepareMediaForGateway(mediaUrls)
            val url = buildUrl("/api/nodes/${node.id}/send")
            val payload = json.encodeToString(
                BaileysSendRequest(
                    recipient = recipientPhone,
                    message = messageText,
                    media = preparedMedia
                )
            )
            val request = Request.Builder()
                .url(url)
                .post(payload.toRequestBody(jsonMediaType))
                .withAuth(config)
                .build()

            httpClient.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val parsedId = extractMessageId(body) ?: "msg-${System.currentTimeMillis()}"
                    return@withContext SendResult(
                        isSuccess = true,
                        messageId = parsedId,
                        errorMessage = null
                    )
                } else {
                    val errorDetail = extractErrorMessage(body) ?: "HTTP ${response.code}: ${response.message}"
                    if (fallbackOnException) {
                        return@withContext fallbackEngine.sendMessage(recipientPhone, messageText, mediaUrls)
                    } else {
                        return@withContext SendResult(
                            isSuccess = false,
                            errorMessage = errorDetail
                        )
                    }
                }
            }
        } catch (e: Exception) {
            if (fallbackOnException) {
                return@withContext fallbackEngine.sendMessage(recipientPhone, messageText, mediaUrls)
            } else {
                return@withContext SendResult(
                    isSuccess = false,
                    errorMessage = e.message ?: "Connection failed"
                )
            }
        }
    }

    override suspend fun sendTypingPresence(recipientPhone: String, durationMs: Long): Unit = withContext(Dispatchers.IO) {
        val config = configProvider()
        if (!config.isEnabled) {
            fallbackEngine.sendTypingPresence(recipientPhone, durationMs)
            return@withContext
        }

        try {
            val url = buildUrl("/api/nodes/${node.id}/presence")
            val payload = json.encodeToString(
                BaileysPresenceRequest(
                    recipient = recipientPhone,
                    durationMs = durationMs
                )
            )
            val request = Request.Builder()
                .url(url)
                .post(payload.toRequestBody(jsonMediaType))
                .withAuth(config)
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful && fallbackOnException) {
                    fallbackEngine.sendTypingPresence(recipientPhone, durationMs)
                    return@withContext
                }
            }
        } catch (_: Exception) {
            if (fallbackOnException) {
                fallbackEngine.sendTypingPresence(recipientPhone, durationMs)
                return@withContext
            }
        }

        // Preserve typing duration delay for humanized delay and UI telemetry
        if (durationMs > 0) {
            delay(durationMs)
        }
    }

    override suspend fun ping(): PingResult = withContext(Dispatchers.IO) {
        val config = configProvider()
        if (!config.isEnabled) {
            return@withContext fallbackEngine.ping()
        }

        val start = System.currentTimeMillis()
        try {
            val url = buildUrl("/api/nodes/${node.id}/status")
            val request = Request.Builder()
                .url(url)
                .get()
                .withAuth(config)
                .build()

            httpClient.newCall(request).execute().use { response ->
                val latency = (System.currentTimeMillis() - start).coerceAtLeast(1L)
                if (response.isSuccessful) {
                    return@withContext PingResult(
                        isSuccess = true,
                        latencyMs = latency,
                        message = "Baileys Bridge Connected"
                    )
                } else {
                    val body = response.body?.string().orEmpty()
                    val errorMsg = extractErrorMessage(body) ?: "HTTP ${response.code}: ${response.message}"
                    if (fallbackOnException) {
                        return@withContext fallbackEngine.ping()
                    } else {
                        return@withContext PingResult(
                            isSuccess = false,
                            latencyMs = latency,
                            message = errorMsg
                        )
                    }
                }
            }
        } catch (e: Exception) {
            val latency = (System.currentTimeMillis() - start).coerceAtLeast(1L)
            if (fallbackOnException) {
                return@withContext fallbackEngine.ping()
            } else {
                return@withContext PingResult(
                    isSuccess = false,
                    latencyMs = latency,
                    message = e.message ?: "Connection error"
                )
            }
        }
    }

    override suspend fun disconnect(): Unit = withContext(Dispatchers.IO) {
        activeWebSocket?.close(1000, "Node disconnecting")
        activeWebSocket = null
        updateConnectionStatus(NodeStatus.DISCONNECTED)

        val config = configProvider()
        if (!config.isEnabled) {
            fallbackEngine.disconnect()
            return@withContext
        }

        try {
            val url = buildUrl("/api/nodes/${node.id}/disconnect")
            val request = Request.Builder()
                .url(url)
                .post("{}".toRequestBody(jsonMediaType))
                .withAuth(config)
                .build()

            httpClient.newCall(request).execute().close()
        } catch (_: Exception) {
            if (fallbackOnException) {
                fallbackEngine.disconnect()
            }
        }
    }

    fun connectWebSocket(customListener: WebSocketListener? = null): WebSocket? {
        val config = configProvider()
        if (!config.isEnabled) return null

        val wsUrl = config.gatewayUrl
            .replaceFirst(Regex("^http://", RegexOption.IGNORE_CASE), "ws://")
            .replaceFirst(Regex("^https://", RegexOption.IGNORE_CASE), "wss://")
            .trimEnd('/') + "/ws/nodes/${node.id}"

        val requestBuilder = Request.Builder().url(wsUrl)
        if (config.apiKey.isNotBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer ${config.apiKey.trim()}")
        }

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                updateConnectionStatus(NodeStatus.CONNECTED)
                customListener?.onOpen(webSocket, response)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val root = json.parseToJsonElement(text).jsonObject
                    val statusText = root["status"]?.jsonPrimitive?.content
                    if (statusText != null) {
                        when (statusText.uppercase()) {
                            "CONNECTED" -> updateConnectionStatus(NodeStatus.CONNECTED)
                            "PAIRING" -> updateConnectionStatus(NodeStatus.PAIRING)
                            "DISCONNECTED" -> updateConnectionStatus(NodeStatus.DISCONNECTED)
                            "ERROR" -> updateConnectionStatus(NodeStatus.ERROR)
                        }
                    }
                } catch (_: Exception) {}
                customListener?.onMessage(webSocket, text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                updateConnectionStatus(NodeStatus.ERROR)
                customListener?.onFailure(webSocket, t, response)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                updateConnectionStatus(NodeStatus.DISCONNECTED)
                customListener?.onClosed(webSocket, code, reason)
            }
        }

        val ws = httpClient.newWebSocket(requestBuilder.build(), listener)
        activeWebSocket = ws
        return ws
    }

    private fun updateConnectionStatus(newStatus: NodeStatus) {
        _connectionState.value = newStatus
        onStatusChanged?.invoke(newStatus)
    }

    private fun buildUrl(path: String): String {
        val base = configProvider().gatewayUrl.trimEnd('/')
        val cleanPath = if (path.startsWith("/")) path else "/$path"
        return "$base$cleanPath"
    }

    private fun Request.Builder.withAuth(config: BaileysConfig): Request.Builder {
        val apiKey = config.apiKey.trim()
        if (apiKey.isNotEmpty()) {
            addHeader("Authorization", "Bearer $apiKey")
        }
        return this
    }

    private fun extractPairingCode(jsonString: String): String? {
        return try {
            val element = json.parseToJsonElement(jsonString).jsonObject
            element["code"]?.jsonPrimitive?.content
                ?: element["pairingCode"]?.jsonPrimitive?.content
                ?: element["data"]?.jsonObject?.get("code")?.jsonPrimitive?.content
        } catch (_: Exception) {
            null
        }
    }

    private fun extractMessageId(jsonString: String): String? {
        return try {
            val element = json.parseToJsonElement(jsonString).jsonObject
            element["messageId"]?.jsonPrimitive?.content
                ?: element["id"]?.jsonPrimitive?.content
                ?: element["key"]?.jsonObject?.get("id")?.jsonPrimitive?.content
        } catch (_: Exception) {
            null
        }
    }

    private fun extractErrorMessage(jsonString: String): String? {
        return try {
            val element = json.parseToJsonElement(jsonString).jsonObject
            element["error"]?.jsonPrimitive?.content
                ?: element["message"]?.jsonPrimitive?.content
        } catch (_: Exception) {
            null
        }
    }

    private val mediaDataUrlCache = java.util.concurrent.ConcurrentHashMap<String, String>()

    private fun prepareMediaForGateway(mediaUrls: List<String>): List<String> {
        val ctx = context
        return mediaUrls.mapNotNull { mediaStr ->
            if (mediaStr.isBlank() || mediaStr.startsWith("dummy://")) {
                null
            } else if (mediaStr.startsWith("http://") || mediaStr.startsWith("https://") || mediaStr.startsWith("data:")) {
                mediaStr
            } else if (ctx != null && (mediaStr.startsWith("content://") || mediaStr.startsWith("file://"))) {
                mediaDataUrlCache.getOrPut(mediaStr) {
                    convertUriToDataUrl(ctx, mediaStr) ?: mediaStr
                }
            } else {
                mediaStr
            }
        }
    }

    private fun convertUriToDataUrl(ctx: android.content.Context, uriString: String): String? {
        return try {
            val uri = android.net.Uri.parse(uriString)
            val resolver = ctx.contentResolver
            val detectedMime = resolver.getType(uri) ?: getMimeFromPath(uriString)
            val fileName = getFileName(ctx, uri) ?: "file_${System.currentTimeMillis()}"

            val (finalBytes, effectiveMime) = if (detectedMime.startsWith("image/")) {
                compressImage(ctx, uri) ?: run {
                    val raw = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
                    Pair(raw, detectedMime)
                }
            } else {
                val raw = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
                Pair(raw, detectedMime)
            }

            if (finalBytes.isNotEmpty()) {
                val base64 = android.util.Base64.encodeToString(finalBytes, android.util.Base64.NO_WRAP)
                "data:$effectiveMime;name=${android.net.Uri.encode(fileName)};base64,$base64"
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun compressImage(ctx: android.content.Context, uri: android.net.Uri): Pair<ByteArray, String>? {
        return try {
            val boundsOptions = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            ctx.contentResolver.openInputStream(uri)?.use {
                android.graphics.BitmapFactory.decodeStream(it, null, boundsOptions)
            }
            val origW = boundsOptions.outWidth
            val origH = boundsOptions.outHeight
            if (origW <= 0 || origH <= 0) return null

            val maxDim = 1920
            var sampleSize = 1
            val maxSide = maxOf(origW, origH)
            if (maxSide > maxDim) {
                sampleSize = (maxSide / maxDim).coerceAtLeast(1)
            }

            val decodeOptions = android.graphics.BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            val sampledBitmap = ctx.contentResolver.openInputStream(uri)?.use {
                android.graphics.BitmapFactory.decodeStream(it, null, decodeOptions)
            } ?: return null

            val w = sampledBitmap.width
            val h = sampledBitmap.height
            val finalBitmap = if (w > maxDim || h > maxDim) {
                val ratio = minOf(maxDim.toFloat() / w, maxDim.toFloat() / h)
                val targetW = (w * ratio).toInt().coerceAtLeast(1)
                val targetH = (h * ratio).toInt().coerceAtLeast(1)
                android.graphics.Bitmap.createScaledBitmap(sampledBitmap, targetW, targetH, true)
            } else {
                sampledBitmap
            }

            val baos = java.io.ByteArrayOutputStream()
            finalBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, baos)
            Pair(baos.toByteArray(), "image/jpeg")
        } catch (_: Throwable) {
            null
        }
    }

    private fun getFileName(ctx: android.content.Context, uri: android.net.Uri): String? {
        if (uri.scheme == "content") {
            try {
                ctx.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        return cursor.getString(nameIndex)
                    }
                }
            } catch (_: Exception) {}
        }
        return uri.lastPathSegment
    }

    private fun getMimeFromPath(path: String): String {
        val lower = path.lowercase()
        return when {
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "image/jpeg"
            lower.endsWith(".png") -> "image/png"
            lower.endsWith(".webp") -> "image/webp"
            lower.endsWith(".gif") -> "image/gif"
            lower.endsWith(".mp4") -> "video/mp4"
            lower.endsWith(".mov") -> "video/quicktime"
            lower.endsWith(".pdf") -> "application/pdf"
            lower.endsWith(".doc") || lower.endsWith(".docx") -> "application/msword"
            lower.endsWith(".xls") || lower.endsWith(".xlsx") -> "application/vnd.ms-excel"
            else -> "application/octet-stream"
        }
    }

    companion object {
        val defaultClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }
    }
}
