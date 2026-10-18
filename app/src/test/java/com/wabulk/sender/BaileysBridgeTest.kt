package com.wabulk.sender

import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.engine.PingResult
import com.wabulk.sender.engine.SendResult
import com.wabulk.sender.engine.SimulatedWhatsAppEngine
import com.wabulk.sender.engine.WhatsAppEngine
import com.wabulk.sender.engine.WhatsAppSessionManager
import com.wabulk.sender.engine.baileys.BaileysBridgeClient
import com.wabulk.sender.engine.baileys.BaileysConfig
import com.wabulk.sender.engine.baileys.BaileysConfigManager
import com.wabulk.sender.engine.baileys.BaileysSessionVault
import com.wabulk.sender.engine.model.NodeSession
import com.wabulk.sender.engine.model.NodeStatus
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class BaileysBridgeTest {

    private val testNode = NodeSession(
        id = "node-personal-01",
        nodeType = AccountNode.PERSONAL,
        phoneNumber = "+628123456789",
        status = NodeStatus.CONNECTED
    )

    @Before
    fun setUp() {
        BaileysConfigManager.reset()
    }

    @After
    fun tearDown() {
        BaileysConfigManager.reset()
    }

    @Test
    fun `BaileysConfigManager updates and resets configuration state`() {
        assertEquals("http://10.0.2.2:3000", BaileysConfigManager.config.value.gatewayUrl)
        assertFalse(BaileysConfigManager.config.value.isEnabled)
        assertEquals("", BaileysConfigManager.config.value.apiKey)

        BaileysConfigManager.setGatewayUrl("https://baileys.mygateway.com:8443")
        BaileysConfigManager.setApiKey("test-secret-key-123")
        BaileysConfigManager.setEnabled(true)
        BaileysConfigManager.setTimeoutSeconds(30L)

        val updated = BaileysConfigManager.config.value
        assertEquals("https://baileys.mygateway.com:8443", updated.gatewayUrl)
        assertEquals("test-secret-key-123", updated.apiKey)
        assertTrue(updated.isEnabled)
        assertEquals(30L, updated.timeoutSeconds)

        BaileysConfigManager.reset()
        assertFalse(BaileysConfigManager.config.value.isEnabled)
    }

    @Test
    fun `disabled BaileysBridgeClient falls back directly to simulated engine`() = runBlocking {
        val config = BaileysConfig(isEnabled = false)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config }
        )

        val ping = client.ping()
        assertTrue(ping.isSuccess)

        val pairCode = client.requestPairingCode("+628123456789")
        assertTrue(pairCode.isNotEmpty())

        val sendResult = client.sendMessage("+628123456789", "Test Message")
        assertTrue(sendResult.isSuccess)
    }

    @Test
    fun `requestPairingCode formats POST url headers and body and parses code`() = runBlocking {
        var recordedRequest: Request? = null
        var recordedBody: String? = null

        val testHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                recordedRequest = request

                val buffer = okio.Buffer()
                request.body?.writeTo(buffer)
                recordedBody = buffer.readUtf8()

                Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body("""{"code":"K97B-42VX"}""".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val config = BaileysConfig(
            gatewayUrl = "http://10.0.2.2:3000/",
            apiKey = "secret-token",
            isEnabled = true
        )

        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = testHttpClient
        )

        val code = client.requestPairingCode("+628123456789")

        assertNotNull(recordedRequest)
        assertEquals("http://10.0.2.2:3000/api/nodes/node-personal-01/pair", recordedRequest?.url.toString())
        assertEquals("POST", recordedRequest?.method)
        assertEquals("Bearer secret-token", recordedRequest?.header("Authorization"))
        assertTrue(recordedBody?.contains("+628123456789") == true)
        assertEquals("K97B-42VX", code)
    }

    @Test
    fun `sendMessage formats payload with media and parses success response`() = runBlocking {
        var recordedRequest: Request? = null
        var recordedBody: String? = null

        val testHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                recordedRequest = request
                val buffer = okio.Buffer()
                request.body?.writeTo(buffer)
                recordedBody = buffer.readUtf8()

                Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body("""{"success":true,"messageId":"baileys-msg-9901"}""".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val config = BaileysConfig(
            gatewayUrl = "http://10.0.2.2:3000",
            apiKey = "my-key",
            isEnabled = true
        )

        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = testHttpClient
        )

        val result = client.sendMessage(
            recipientPhone = "+628999888777",
            messageText = "Hello from Android!",
            mediaUrls = listOf("https://example.com/promo.jpg")
        )

        assertEquals("http://10.0.2.2:3000/api/nodes/node-personal-01/send", recordedRequest?.url.toString())
        assertTrue(recordedBody?.contains("+628999888777") == true)
        assertTrue(recordedBody?.contains("Hello from Android!") == true)
        assertTrue(recordedBody?.contains("https://example.com/promo.jpg") == true)
        assertTrue(result.isSuccess)
        assertEquals("baileys-msg-9901", result.messageId)
    }

    @Test
    fun `sendTypingPresence dispatches presence event to gateway`() = runBlocking {
        var recordedRequest: Request? = null
        var recordedBody: String? = null

        val testHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                recordedRequest = request
                val buffer = okio.Buffer()
                request.body?.writeTo(buffer)
                recordedBody = buffer.readUtf8()

                Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body("""{"success":true}""".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val config = BaileysConfig(gatewayUrl = "http://10.0.2.2:3000", isEnabled = true)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = testHttpClient
        )

        val start = System.currentTimeMillis()
        client.sendTypingPresence("+628111222333", 50L)
        val elapsed = System.currentTimeMillis() - start

        assertEquals("http://10.0.2.2:3000/api/nodes/node-personal-01/presence", recordedRequest?.url.toString())
        assertTrue(recordedBody?.contains("+628111222333") == true)
        assertTrue(recordedBody?.contains("50") == true)
        assertTrue("Typing duration delay must be preserved (elapsed: $elapsed ms)", elapsed >= 40L)
    }

    @Test
    fun `ping queries status endpoint and computes latency`() = runBlocking {
        var recordedRequest: Request? = null

        val testHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                recordedRequest = chain.request()
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body("""{"status":"CONNECTED","uptime":1234}""".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val config = BaileysConfig(gatewayUrl = "http://10.0.2.2:3000", isEnabled = true)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = testHttpClient
        )

        val pingResult = client.ping()

        assertEquals("http://10.0.2.2:3000/api/nodes/node-personal-01/status", recordedRequest?.url.toString())
        assertTrue(pingResult.isSuccess)
        assertEquals("Baileys Bridge Connected", pingResult.message)
    }

    @Test
    fun `disconnect dispatches POST disconnect to gateway`() = runBlocking {
        var recordedRequest: Request? = null

        val testHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                recordedRequest = chain.request()
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body("""{"success":true}""".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val config = BaileysConfig(gatewayUrl = "http://10.0.2.2:3000", isEnabled = true)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = testHttpClient
        )

        client.disconnect()

        assertEquals("http://10.0.2.2:3000/api/nodes/node-personal-01/disconnect", recordedRequest?.url.toString())
        assertEquals("POST", recordedRequest?.method)
    }

    @Test
    fun `gateway connection failure falls back to simulated engine when fallback is enabled`() = runBlocking {
        val failingHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                throw IOException("Network unreachable: connection refused")
            }
            .build()

        val config = BaileysConfig(gatewayUrl = "http://10.0.2.2:3000", isEnabled = true)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = failingHttpClient,
            fallbackOnException = true
        )

        val sendResult = client.sendMessage("+628123456789", "Fallback Test")
        assertTrue("When fallback is enabled on exception, should succeed via simulated fallback", sendResult.isSuccess)

        val pairResult = client.requestPairingCode("+628123456789")
        assertTrue("Pairing code should be returned via simulated fallback", pairResult.isNotEmpty())
    }

    @Test
    fun `gateway connection failure returns structured error when fallback is disabled`() = runBlocking {
        val failingHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                throw IOException("Connection timed out after 15000ms")
            }
            .build()

        val config = BaileysConfig(gatewayUrl = "http://10.0.2.2:3000", isEnabled = true)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = failingHttpClient,
            fallbackOnException = false
        )

        val sendResult = client.sendMessage("+628123456789", "No Fallback Test")
        assertFalse(sendResult.isSuccess)
        assertTrue(sendResult.errorMessage?.contains("Connection timed out") == true)

        val pingResult = client.ping()
        assertFalse(pingResult.isSuccess)
        assertTrue(pingResult.message.contains("Connection timed out"))
    }

    @Test
    fun `BaileysBridgeClient defaults fallbackOnException to false and reports gateway HTTP error without masking`() = runBlocking {
        val errorHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(502)
                    .message("Bad Gateway")
                    .body("""{"error":"Baileys socket session closed by remote"}""".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val config = BaileysConfig(gatewayUrl = "http://10.0.2.2:3000", isEnabled = true)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = errorHttpClient
        )

        assertFalse("fallbackOnException must default to false", client.fallbackOnException)

        val sendResult = client.sendMessage("+628123456789", "Gateway error message")
        assertFalse(sendResult.isSuccess)
        assertEquals("Baileys socket session closed by remote", sendResult.errorMessage)
    }

    @Test
    fun `WhatsAppSessionManager resolves BaileysBridgeClient when enabled and Simulated when disabled`() {
        val sessionManager = WhatsAppSessionManager()

        BaileysConfigManager.setEnabled(false)
        val engineDisabled = sessionManager.getEngineForNode(AccountNode.PERSONAL)
        assertTrue("Expected SimulatedWhatsAppEngine when disabled", engineDisabled is SimulatedWhatsAppEngine)

        BaileysConfigManager.setEnabled(true)
        val engineEnabled = sessionManager.getEngineForNode(AccountNode.PERSONAL)
        assertTrue("Expected BaileysBridgeClient when enabled", engineEnabled is BaileysBridgeClient)
    }

    @Test
    fun `request omits Authorization header when apiKey is blank`() = runBlocking {
        var recordedRequest: Request? = null

        val testHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                recordedRequest = chain.request()
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body("""{"status":"CONNECTED"}""".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val config = BaileysConfig(gatewayUrl = "http://10.0.2.2:3000", apiKey = "", isEnabled = true)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = testHttpClient
        )

        client.ping()
        org.junit.Assert.assertNull(recordedRequest?.header("Authorization"))
    }

    @Test
    fun `connectWebSocket returns null when isEnabled is false`() {
        val config = BaileysConfig(isEnabled = false)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config }
        )
        val ws = client.connectWebSocket()
        org.junit.Assert.assertNull(ws)
    }

    @Test
    fun `sendMessage handles HTTP 400 server error and returns structured failure`() = runBlocking {
        val testHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(400)
                    .message("Bad Request")
                    .body("""{"error":"Invalid phone number format"}""".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val config = BaileysConfig(gatewayUrl = "http://10.0.2.2:3000", isEnabled = true)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = testHttpClient,
            fallbackOnException = false
        )

        val result = client.sendMessage("invalid-phone", "test message")
        assertFalse(result.isSuccess)
        assertEquals("Invalid phone number format", result.errorMessage)
    }

    @Test
    fun `requestPairingCodeRemote on WhatsAppSessionManager updates session status and pairing code`() = runBlocking {
        val testHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body("""{"code":"XYZ9-8765"}""".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        BaileysConfigManager.updateConfig(
            BaileysConfig(gatewayUrl = "http://10.0.2.2:3000", isEnabled = true)
        )

        val sessionManager = WhatsAppSessionManager(
            engine = BaileysBridgeClient(
                node = testNode,
                configProvider = { BaileysConfigManager.config.value },
                httpClient = testHttpClient
            )
        )

        val code = sessionManager.requestPairingCodeRemote(AccountNode.PERSONAL, "+628123456789")
        assertEquals("XYZ9-8765", code)
        assertEquals(NodeStatus.PAIRING, sessionManager.node1Session.value.status)
        assertEquals("XYZ9-8765", sessionManager.node1Session.value.pairingCode)
    }

    @Test
    fun `WhatsAppSessionManager caches BaileysBridgeClient instances per node`() {
        BaileysConfigManager.setEnabled(true)
        val sessionManager = WhatsAppSessionManager()

        val engine1First = sessionManager.getEngineForNode(AccountNode.PERSONAL)
        val engine1Second = sessionManager.getEngineForNode(AccountNode.PERSONAL)
        org.junit.Assert.assertSame("Personal node engine must be cached and reused", engine1First, engine1Second)

        val engine2First = sessionManager.getEngineForNode(AccountNode.BUSINESS)
        val engine2Second = sessionManager.getEngineForNode(AccountNode.BUSINESS)
        org.junit.Assert.assertSame("Business node engine must be cached and reused", engine2First, engine2Second)
        org.junit.Assert.assertNotSame("Personal and Business engines must be distinct instances", engine1First, engine2First)
    }

    @Test
    fun `BaileysBridgeClient status changes propagate to WhatsAppSessionManager session StateFlow`() = runBlocking {
        BaileysConfigManager.setEnabled(true)
        val sessionManager = WhatsAppSessionManager()

        val engine = sessionManager.getOrCreateBaileysEngine(AccountNode.PERSONAL)
        assertEquals(NodeStatus.CONNECTED, sessionManager.node1Session.value.status)

        // Disconnect updates session status to DISCONNECTED via status callback
        engine.disconnect()
        assertEquals(NodeStatus.DISCONNECTED, sessionManager.node1Session.value.status)
    }

    @Test
    fun `backupSession stores credentials into BaileysSessionVault when export succeeds`() = runBlocking {
        val testHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                if (request.url.encodedPath.endsWith("/session") && request.method == "GET") {
                    Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body("""{"success":true,"nodeId":"node-personal-01","phoneNumber":"628123","files":{"creds.json":"{\"registered\":true}"}}""".toResponseBody("application/json".toMediaType()))
                        .build()
                } else {
                    Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(404)
                        .message("Not Found")
                        .body("{}".toResponseBody("application/json".toMediaType()))
                        .build()
                }
            }
            .build()

        val config = BaileysConfig(gatewayUrl = "http://10.0.2.2:3000", isEnabled = true)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = testHttpClient,
            autoBackupSession = false
        )

        val backedUp = client.backupSession()
        assertTrue("backupSession must return true when creds.json is returned", backedUp)
        assertTrue("Vault must have session for testNode", BaileysSessionVault.hasSession(null, testNode.id))
        val saved = BaileysSessionVault.getSession(null, testNode.id)
        assertNotNull(saved)
        assertTrue(saved!!.contains("creds.json"))
    }

    @Test
    fun `restoreSession auto-restores session from BaileysSessionVault when node is disconnected`() = runBlocking {
        BaileysSessionVault.saveSession(
            null,
            testNode.id,
            """{"success":true,"phoneNumber":"628123","files":{"creds.json":"{}"}}"""
        )

        var restoredPayload: String? = null
        val testHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                if (request.url.encodedPath.endsWith("/session") && request.method == "POST") {
                    val buffer = okio.Buffer()
                    request.body?.writeTo(buffer)
                    restoredPayload = buffer.readUtf8()
                    Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body("""{"success":true,"status":"CONNECTED"}""".toResponseBody("application/json".toMediaType()))
                        .build()
                } else {
                    Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body("""{"status":"DISCONNECTED","registered":false}""".toResponseBody("application/json".toMediaType()))
                        .build()
                }
            }
            .build()

        val config = BaileysConfig(gatewayUrl = "http://10.0.2.2:3000", isEnabled = true)
        val client = BaileysBridgeClient(
            node = testNode,
            configProvider = { config },
            httpClient = testHttpClient,
            autoBackupSession = false
        )

        val pingResult = client.ping()
        assertTrue("ping must succeed when session is auto-restored", pingResult.isSuccess)
        assertNotNull("restore payload must have been sent to POST /api/nodes/:nodeId/session", restoredPayload)
        assertTrue(restoredPayload!!.contains("creds.json"))
    }
}
