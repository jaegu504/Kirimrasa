package com.wabulk.sender

import com.wabulk.sender.data.local.db.CampaignDao
import com.wabulk.sender.data.local.db.CampaignEntity
import com.wabulk.sender.data.local.db.CampaignItemEntity
import com.wabulk.sender.data.local.db.CampaignItemStatus
import com.wabulk.sender.data.local.db.CampaignStatus
import com.wabulk.sender.data.local.db.CampaignWithItems
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.domain.model.MessageBubble
import com.wabulk.sender.engine.WhatsAppSessionManager
import com.wabulk.sender.engine.queue.DispatchQueueManager
import com.wabulk.sender.engine.queue.model.QueueItem
import com.wabulk.sender.engine.queue.model.QueueTaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeQueueCampaignDao : CampaignDao {
    val campaigns = mutableListOf<CampaignEntity>()
    val items = mutableListOf<CampaignItemEntity>()
    private val campaignsFlow = MutableStateFlow<List<CampaignEntity>>(emptyList())
    private val itemsFlow = MutableStateFlow<List<CampaignItemEntity>>(emptyList())

    private fun updateFlows() {
        campaignsFlow.value = campaigns.sortedByDescending { it.createdAt }.toList()
        itemsFlow.value = items.sortedBy { it.id }.toList()
    }

    override suspend fun insertCampaign(campaign: CampaignEntity): Long {
        val newId = (campaigns.maxOfOrNull { it.id } ?: 0L) + 1L
        val saved = campaign.copy(id = newId)
        campaigns.add(saved)
        updateFlows()
        return newId
    }

    override suspend fun insertItems(items: List<CampaignItemEntity>): List<Long> {
        val ids = mutableListOf<Long>()
        var currentMax = this.items.maxOfOrNull { it.id } ?: 0L
        for (item in items) {
            currentMax += 1L
            this.items.add(item.copy(id = currentMax))
            ids.add(currentMax)
        }
        updateFlows()
        return ids
    }

    override suspend fun updateCampaign(campaign: CampaignEntity) {
        val index = campaigns.indexOfFirst { it.id == campaign.id }
        if (index >= 0) {
            campaigns[index] = campaign
            updateFlows()
        }
    }

    override suspend fun updateItem(item: CampaignItemEntity) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            items[index] = item
            updateFlows()
        }
    }

    override suspend fun updateCampaignItemStatus(
        id: Long,
        status: String,
        errorMessage: String?,
        sentAt: Long
    ) {
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0) {
            items[index] = items[index].copy(
                status = status,
                errorMessage = errorMessage,
                sentAt = sentAt
            )
            updateFlows()
        }
    }

    override fun observeAllCampaigns(): Flow<List<CampaignEntity>> = campaignsFlow

    override suspend fun getCampaignById(id: Long): CampaignEntity? {
        return campaigns.find { it.id == id }
    }

    override suspend fun getItemsForCampaign(campaignId: Long): List<CampaignItemEntity> {
        return items.filter { it.campaignId == campaignId }
    }

    override fun observeItemsForCampaign(campaignId: Long): Flow<List<CampaignItemEntity>> {
        return itemsFlow.map { list -> list.filter { it.campaignId == campaignId } }
    }

    override suspend fun getRecentCampaigns(limit: Int): List<CampaignEntity> {
        return campaigns.sortedByDescending { it.createdAt }.take(limit)
    }

    override suspend fun getCampaignWithItems(id: Long): CampaignWithItems? {
        val campaign = campaigns.find { it.id == id } ?: return null
        val campaignItems = items.filter { it.campaignId == id }
        return CampaignWithItems(campaign, campaignItems)
    }

    override suspend fun deleteCampaign(id: Long) {
        campaigns.removeAll { it.id == id }
        items.removeAll { it.campaignId == id }
        updateFlows()
    }
}

class DispatchQueueManagerTest {

    private lateinit var fakeDao: FakeQueueCampaignDao
    private lateinit var manager: DispatchQueueManager

    @Before
    fun setUp() {
        fakeDao = FakeQueueCampaignDao()
        manager = DispatchQueueManager.resetForTesting(fakeDao)
        manager.skipDelayForTesting = true
    }

    @Test
    fun `buildQueueItem preserves per-contact company and custom1 when present`() {
        val contact = ContactEntity(
            id = 101L,
            rawName = "Farhan Syahputra",
            phoneNumber = "+6281234567890",
            customNickname = "Farhan",
            company = "PT Digital Solusindo",
            custom1 = "DISCOUNT70"
        )
        val bubbles = listOf(
            MessageBubble(text = "Halo {Nickname}, dari {Company}! Kode Anda: {Custom_1}.")
        )

        val queueItem = DispatchQueueManager.buildQueueItem(
            contact = contact,
            bubbles = bubbles,
            defaultCompany = "KirimRasa",
            defaultCustom1 = "PROMO30"
        )

        assertEquals("Farhan Syahputra", queueItem.recipientName)
        assertEquals("+6281234567890", queueItem.phoneNumber)
        assertEquals(1, queueItem.bubbleTexts.size)
        assertEquals("Halo Farhan, dari PT Digital Solusindo! Kode Anda: DISCOUNT70.", queueItem.bubbleTexts[0])
    }

    @Test
    fun `buildQueueItem falls back to composer globals when contact variables are blank`() {
        val contact = ContactEntity(
            id = 102L,
            rawName = "Dewi Lestari",
            phoneNumber = "+6281987654321",
            customNickname = "Dewi",
            company = "",
            custom1 = ""
        )
        val bubbles = listOf(
            MessageBubble(text = "Halo {Nickname}, promo {Company}! Kode: {Custom_1}.")
        )

        val queueItem = DispatchQueueManager.buildQueueItem(
            contact = contact,
            bubbles = bubbles,
            defaultCompany = "KirimRasa",
            defaultCustom1 = "PROMO30"
        )

        assertEquals("Halo Dewi, promo KirimRasa! Kode: PROMO30.", queueItem.bubbleTexts[0])
    }

    @Test
    fun `enqueueBroadcast inserts CampaignEntity and initial PENDING items to SQLite`() = runBlocking {
        val items = listOf(
            QueueItem(
                contactId = 1L,
                recipientName = "Alice",
                phoneNumber = "+628111111",
                bubbleTexts = listOf("Msg 1"),
                status = QueueTaskStatus.PENDING
            ),
            QueueItem(
                contactId = 2L,
                recipientName = "Bob",
                phoneNumber = "+628222222",
                bubbleTexts = listOf("Msg 2"),
                status = QueueTaskStatus.PENDING
            )
        )

        manager.enqueueBroadcast(
            items = items,
            campaignTitle = "Flash Promo Ramadhan",
            campaignDao = fakeDao
        )

        // Wait a tick for background insertion coroutine
        kotlinx.coroutines.delay(100)

        assertEquals(1, fakeDao.campaigns.size)
        val campaign = fakeDao.campaigns[0]
        assertEquals("Flash Promo Ramadhan", campaign.title)
        assertEquals(2, campaign.totalRecipients)
        assertEquals(CampaignStatus.RUNNING.name, campaign.status)

        val dbItems = fakeDao.items
        assertEquals(2, dbItems.size)
        assertEquals("Alice", dbItems[0].recipientName)
        assertEquals(CampaignItemStatus.PENDING.name, dbItems[0].status)
        assertEquals("Bob", dbItems[1].recipientName)
        assertEquals(CampaignItemStatus.PENDING.name, dbItems[1].status)
    }

    @Test
    fun `dispatch execution loop updates item status to DELIVERED and marks campaign COMPLETED`() = runBlocking {
        val items = listOf(
            QueueItem(
                contactId = 1L,
                recipientName = "User One",
                phoneNumber = "+628111111",
                bubbleTexts = listOf("Message One"),
                nodeUsed = AccountNode.PERSONAL,
                status = QueueTaskStatus.PENDING
            )
        )

        manager.enqueueBroadcast(
            items = items,
            campaignTitle = "Automated Test Blast",
            campaignDao = fakeDao
        )

        // Start dispatch
        manager.startDispatch(null)

        // Wait for loop to complete with skipDelayForTesting = true
        var attempts = 0
        while (manager.isRunning.value && attempts < 50) {
            kotlinx.coroutines.delay(50)
            attempts++
        }

        kotlinx.coroutines.delay(100)

        // Verify queue item completed
        assertEquals(1, manager.sentCount)
        assertEquals(0, manager.remainingCount)

        // Verify SQLite telemetry updated
        val campaign = fakeDao.campaigns.firstOrNull()
        assertNotNull(campaign)
        assertEquals(CampaignStatus.COMPLETED.name, campaign?.status)
        assertEquals(1, campaign?.sentCount)
        assertEquals(0, campaign?.failedCount)

        val dbItem = fakeDao.items.firstOrNull()
        assertNotNull(dbItem)
        assertEquals(CampaignItemStatus.SENT.name, dbItem?.status)
    }

    @Test
    fun `stopDispatch marks running campaign as CANCELLED in SQLite`() = runBlocking {
        val items = listOf(
            QueueItem(
                contactId = 1L,
                recipientName = "Pending User",
                phoneNumber = "+628999999",
                bubbleTexts = listOf("Waiting..."),
                status = QueueTaskStatus.PENDING
            )
        )

        manager.enqueueBroadcast(
            items = items,
            campaignTitle = "Cancelled Campaign",
            campaignDao = fakeDao
        )

        kotlinx.coroutines.delay(100)

        manager.stopDispatch(null)

        kotlinx.coroutines.delay(100)

        val campaign = fakeDao.campaigns.firstOrNull()
        assertNotNull(campaign)
        assertEquals(CampaignStatus.CANCELLED.name, campaign?.status)
    }
}
