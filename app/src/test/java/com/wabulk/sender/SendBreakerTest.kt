package com.wabulk.sender

import com.wabulk.sender.data.locale.LocalizationManager
import com.wabulk.sender.engine.WhatsAppSessionManager
import com.wabulk.sender.engine.baileys.BaileysBridgeClient
import com.wabulk.sender.engine.baileys.BaileysConfig
import com.wabulk.sender.engine.baileys.BaileysConfigManager
import com.wabulk.sender.engine.queue.DispatchQueueManager
import com.wabulk.sender.engine.queue.model.QueueItem
import com.wabulk.sender.engine.queue.model.QueueTaskStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Gateway answers that should pause sending, and the ones that should not. */
class SendBreakerTest {

    private lateinit var manager: DispatchQueueManager
    private val sendsTo = mutableListOf<String>()

    /** Answers /send with the status and body [answer] picks for the recipient; everything else with 200 {}. */
    private fun gateway(answer: (String) -> Pair<Int, String>) = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            val (code, body) = if (request.url.encodedPath.endsWith("/send")) {
                val payload = Buffer().also { request.body!!.writeTo(it) }.readUtf8()
                val recipient = Regex("\"recipient\":\"([^\"]+)\"").find(payload)!!.groupValues[1]
                sendsTo.add(recipient)
                answer(recipient)
            } else {
                200 to "{}"
            }
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message("x")
                .body(body.toResponseBody("application/json".toMediaType()))
                .build()
        }
        .build()

    private fun useGateway(answer: (String) -> Pair<Int, String>) {
        val sessions = WhatsAppSessionManager.getInstance()
        sessions.setPersonalEngineForTesting(
            BaileysBridgeClient(
                node = sessions.node1Session.value,
                configProvider = { BaileysConfigManager.config.value },
                httpClient = gateway(answer)
            )
        )
    }

    private fun item(phone: String, bubbles: Int = 1) = QueueItem(
        contactId = phone.hashCode().toLong(),
        recipientName = phone,
        phoneNumber = phone,
        bubbleTexts = List(bubbles) { "Halo $it" },
        status = QueueTaskStatus.PENDING
    )

    private suspend fun runUntilIdle() {
        var waited = 0
        while (manager.isRunning.value && !manager.isPaused.value && waited < 100) {
            delay(30)
            waited++
        }
        delay(50)
    }

    @Before
    fun setUp() {
        BaileysConfigManager.setGatewayUrl("http://gateway.test")
        BaileysConfigManager.setEnabled(true)
        manager = DispatchQueueManager.resetForTesting(null)
        manager.skipDelayForTesting = true
    }

    @After
    fun tearDown() {
        manager.stopDispatch(null)
        WhatsAppSessionManager.getInstance().setPersonalEngineForTesting(null)
        BaileysConfigManager.reset()
    }

    @Test
    fun `five failures in a row pause sending, numbers without WhatsApp do not count`() = runBlocking {
        useGateway { phone ->
            if (phone.endsWith("0")) 422 to """{"error":"x","code":"NOT_ON_WHATSAPP"}"""
            else 500 to """{"error":"Gagal mengirim pesan: timeout"}"""
        }
        // The two-bubble contact without WhatsApp gets one lookup, not two sends
        val items = listOf(item("+62810", bubbles = 2)) + (1..5).map { item("+6281$it") } + item("+62899")
        manager.enqueueBroadcast(items = items, campaignTitle = "Breaker", campaignDao = null)
        manager.startDispatch(null)
        runUntilIdle()

        assertTrue(manager.isPaused.value)
        assertEquals(LocalizationManager.activeStrings.pausedFailures(5), manager.pauseReason.value)
        assertEquals(listOf("+62810", "+62811", "+62812", "+62813", "+62814", "+62815"), sendsTo)
        val byPhone = manager.queueItems.value.associateBy { it.phoneNumber }
        assertEquals(LocalizationManager.activeStrings.notOnWhatsApp, byPhone["+62810"]?.errorMessage)
        assertEquals(QueueTaskStatus.PENDING, byPhone["+62899"]?.status)

        manager.resumeDispatch()
        assertEquals(null, manager.pauseReason.value)
    }

    @Test
    fun `a refused account pauses at once with its reason`() = runBlocking {
        useGateway { 409 to """{"error":"x","code":"FORBIDDEN"}""" }
        manager.enqueueBroadcast(items = listOf(item("+62811"), item("+62812")), campaignTitle = "Refused", campaignDao = null)
        manager.startDispatch(null)
        runUntilIdle()

        assertTrue(manager.isPaused.value)
        assertEquals(LocalizationManager.activeStrings.pausedForbidden, manager.pauseReason.value)
        assertEquals(listOf("+62811"), sendsTo)
    }

    @Test
    fun `a WhatsApp restriction pauses once, successes keep going`() = runBlocking {
        val until = System.currentTimeMillis() + 3_600_000
        useGateway { phone ->
            if (phone == "+62812") 200 to """{"success":true,"messageId":"m","restrictedUntil":$until}"""
            else 200 to """{"success":true,"messageId":"m","restrictedUntil":null}"""
        }
        manager.enqueueBroadcast(items = (1..3).map { item("+6281$it") }, campaignTitle = "Lock", campaignDao = null)
        manager.startDispatch(null)
        runUntilIdle()

        assertTrue(manager.isPaused.value)
        assertTrue(manager.pauseReason.value!!.isNotBlank())
        assertEquals(2, manager.sentCount)

        manager.resumeDispatch()
        runUntilIdle()
        assertFalse(manager.isRunning.value)
        assertEquals(3, manager.sentCount)
    }

    @Test
    fun `an empty server address says where to fill it in`() = runBlocking {
        val client = BaileysBridgeClient(
            node = WhatsAppSessionManager.getInstance().node1Session.value,
            configProvider = { BaileysConfig(gatewayUrl = "", isEnabled = true) }
        )
        val result = client.sendMessage("+62811", "Halo", emptyList())
        assertFalse(result.isSuccess)
        assertEquals(LocalizationManager.activeStrings.serverNotSet, result.errorMessage)
    }
}
