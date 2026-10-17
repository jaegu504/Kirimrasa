package com.wabulk.sender.data.remote

import com.wabulk.sender.data.remote.dto.AiRewriteRequest
import com.wabulk.sender.data.remote.dto.AiRewriteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class CloudflareAiApi(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build(),
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }
) {
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    suspend fun rewrite(endpointUrl: String, requestDto: AiRewriteRequest): Result<AiRewriteResponse> =
        withContext(Dispatchers.IO) {
            try {
                val requestBodyString = json.encodeToString(requestDto)
                val request = Request.Builder()
                    .url(endpointUrl)
                    .post(requestBodyString.toRequestBody(JSON_MEDIA_TYPE))
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseBodyString = response.body?.string() ?: ""
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            IOException("Server error ${response.code}: $responseBodyString")
                        )
                    }

                    val bubbles = parseBubbles(responseBodyString)
                    if (bubbles.isEmpty()) {
                        val errorMsg = try {
                            val obj = json.parseToJsonElement(responseBodyString).jsonObject
                            obj["error"]?.jsonPrimitive?.content
                        } catch (_: Exception) {
                            null
                        }
                        if (errorMsg != null) {
                            return@withContext Result.failure(IOException(errorMsg))
                        }
                    }

                    Result.success(
                        AiRewriteResponse(
                            success = true,
                            bubbles = bubbles,
                            tone = requestDto.tone
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun parseBubbles(rawResponse: String): List<String> {
        val trimmed = rawResponse.trim()
        if (trimmed.isEmpty()) return emptyList()

        try {
            val rootElement = json.parseToJsonElement(trimmed)

            // Case 1: Direct JSON array: ["bubble 1", "bubble 2"]
            if (rootElement is JsonArray) {
                return rootElement.mapNotNull { it.jsonPrimitive.content.trim().ifEmpty { null } }
            }

            if (rootElement is JsonObject) {
                // Case 2: Standard { "bubbles": ["..."] }
                if (rootElement.containsKey("bubbles")) {
                    val bubblesArray = rootElement["bubbles"]?.jsonArray
                    if (bubblesArray != null) {
                        return bubblesArray.mapNotNull { it.jsonPrimitive.content.trim().ifEmpty { null } }
                    }
                }

                // Case 3: Sapa CRM / Groq format: { "choices": [ { "message": { "content": "..." } } ] }
                if (rootElement.containsKey("choices")) {
                    val choices = rootElement["choices"]?.jsonArray
                    val content = choices?.firstOrNull()?.jsonObject?.get("message")?.jsonObject?.get("content")?.jsonPrimitive?.content
                    if (!content.isNullOrBlank()) {
                        return parseContentString(content)
                    }
                }

                // Case 4: Sapa CRM / Gemini format: { "candidates": [ { "content": { "parts": [ { "text": "..." } ] } } ] }
                if (rootElement.containsKey("candidates")) {
                    val candidates = rootElement["candidates"]?.jsonArray
                    val parts = candidates?.firstOrNull()?.jsonObject?.get("content")?.jsonObject?.get("parts")?.jsonArray
                    val text = parts?.firstOrNull()?.jsonObject?.get("text")?.jsonPrimitive?.content
                    if (!text.isNullOrBlank()) {
                        return parseContentString(text)
                    }
                }
            }
        } catch (_: Exception) {
            // Not a JSON or error parsing JSON element, fallback to string parsing
        }

        return parseContentString(trimmed)
    }

    private fun parseContentString(rawText: String): List<String> {
        val cleaned = rawText
            .replace(Regex("""^```(json)?\s*""", RegexOption.MULTILINE), "")
            .replace(Regex("""```\s*$""", RegexOption.MULTILINE), "")
            .trim()

        // Check if cleaned string is a JSON array
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            try {
                val element = json.parseToJsonElement(cleaned)
                if (element is JsonArray) {
                    return element.mapNotNull { it.jsonPrimitive.content.trim().ifEmpty { null } }
                }
            } catch (_: Exception) {}
        }

        // Check if cleaned string is a JSON object with "bubbles"
        if (cleaned.startsWith("{") && cleaned.endsWith("}")) {
            try {
                val element = json.parseToJsonElement(cleaned)
                if (element is JsonObject && element.containsKey("bubbles")) {
                    val array = element["bubbles"]?.jsonArray
                    if (array != null) {
                        return array.mapNotNull { it.jsonPrimitive.content.trim().ifEmpty { null } }
                    }
                }
            } catch (_: Exception) {}
        }

        // Fallback: split by double newlines or numbered lines
        val lines = cleaned.split(Regex("""\n\s*\n"""))
            .map { it.replace(Regex("""^(\d+\.|\*|-)\s*"""), "").trim() }
            .filter { it.isNotEmpty() }

        return lines.ifEmpty { listOf(cleaned) }
    }
}
