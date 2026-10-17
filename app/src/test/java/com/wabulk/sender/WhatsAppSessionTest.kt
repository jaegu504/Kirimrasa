package com.wabulk.sender

import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.engine.WhatsAppSessionManager
import com.wabulk.sender.engine.model.NodeStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WhatsAppSessionTest {

    private lateinit var sessionManager: WhatsAppSessionManager

    @Before
    fun setup() {
        sessionManager = WhatsAppSessionManager()
    }

    @Test
    fun `generatePairingCode creates valid 8-digit formatted code`() {
        val code = WhatsAppSessionManager.generatePairingCode()
        val regex = Regex("^[A-Z0-9]{4}-[A-Z0-9]{4}$")
        assertTrue("Code '$code' must match pattern XXXX-XXXX", regex.matches(code))
    }

    @Test
    fun `node sessions initialize with expected default statuses`() = runBlocking {
        val node1 = sessionManager.node1Session.first()
        val node2 = sessionManager.node2Session.first()

        assertEquals(AccountNode.PERSONAL, node1.nodeType)
        assertEquals(NodeStatus.CONNECTED, node1.status)

        assertEquals(AccountNode.BUSINESS, node2.nodeType)
        assertEquals(NodeStatus.PAIRING, node2.status)
        assertNotNull(node2.pairingCode)
    }

    @Test
    fun `requestNewPairingCode updates code and resets timer`() = runBlocking {
        val initialCode = sessionManager.node2Session.first().pairingCode
        val newCode = sessionManager.requestNewPairingCode(AccountNode.BUSINESS)

        assertTrue(newCode.isNotEmpty())
        val updatedSession = sessionManager.node2Session.first()
        assertEquals(newCode, updatedSession.pairingCode)
        assertEquals(NodeStatus.PAIRING, updatedSession.status)
        assertTrue(updatedSession.codeExpiresAt > System.currentTimeMillis())
    }

    @Test
    fun `disconnectNode transitions session to DISCONNECTED`() = runBlocking {
        sessionManager.disconnectNode(AccountNode.PERSONAL)
        val session = sessionManager.node1Session.first()
        assertEquals(NodeStatus.DISCONNECTED, session.status)
    }

    @Test
    fun `connectNode transitions session to CONNECTED`() = runBlocking {
        sessionManager.connectNode(AccountNode.BUSINESS, "+628123456789")
        val session = sessionManager.node2Session.first()
        assertEquals(NodeStatus.CONNECTED, session.status)
        assertEquals("+628123456789", session.phoneNumber)
    }

    @Test
    fun `pingNode returns true when node is connected`() = runBlocking {
        val pingSuccess = sessionManager.pingNode(AccountNode.PERSONAL)
        assertTrue(pingSuccess)
    }
}
