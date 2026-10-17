package com.wabulk.sender

import com.wabulk.sender.data.local.db.AppDatabase
import com.wabulk.sender.data.local.db.CampaignDao
import com.wabulk.sender.data.local.db.CampaignEntity
import com.wabulk.sender.data.local.db.CampaignItemEntity
import com.wabulk.sender.data.local.db.CampaignWithItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeCampaignDao : CampaignDao {
    private val campaigns = mutableListOf<CampaignEntity>()
    private val items = mutableListOf<CampaignItemEntity>()
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

class CampaignDatabaseTest {

    private lateinit var campaignDao: FakeCampaignDao

    @Before
    fun setUp() {
        campaignDao = FakeCampaignDao()
    }

    @Test
    fun `campaign entity default values and creation`() {
        val campaign = CampaignEntity(
            title = "Promo Ramadhan 2026",
            totalRecipients = 150
        )

        assertEquals("Promo Ramadhan 2026", campaign.title)
        assertEquals(150, campaign.totalRecipients)
        assertEquals(0, campaign.sentCount)
        assertEquals(0, campaign.failedCount)
        assertEquals("COMPLETED", campaign.status)
        assertEquals("WA Personal", campaign.nodeUsed)
        assertEquals(0L, campaign.durationSeconds)
        assertTrue(campaign.createdAt > 0L)
    }

    @Test
    fun `campaign item entity creation and properties`() {
        val item = CampaignItemEntity(
            campaignId = 1L,
            recipientPhone = "+628123456789",
            recipientName = "Ahmad Dani",
            messageText = "Halo Ahmad, cek promo Ramadhan!",
            status = "PENDING"
        )

        assertEquals(1L, item.campaignId)
        assertEquals("+628123456789", item.recipientPhone)
        assertEquals("Ahmad Dani", item.recipientName)
        assertEquals("Halo Ahmad, cek promo Ramadhan!", item.messageText)
        assertEquals("PENDING", item.status)
        assertNull(item.errorMessage)
        assertTrue(item.sentAt > 0L)
    }

    @Test
    fun `insert campaign and child items contract`() = runTest {
        val campaign = CampaignEntity(
            title = "Flash Sale Weekend",
            totalRecipients = 2,
            status = "RUNNING"
        )

        val campaignId = campaignDao.insertCampaign(campaign)
        assertEquals(1L, campaignId)

        val items = listOf(
            CampaignItemEntity(
                campaignId = campaignId,
                recipientPhone = "+6281111111",
                recipientName = "Budi",
                messageText = "Halo Budi!",
                status = "SENT"
            ),
            CampaignItemEntity(
                campaignId = campaignId,
                recipientPhone = "+6282222222",
                recipientName = "Citra",
                messageText = "Halo Citra!",
                status = "FAILED",
                errorMessage = "Number not registered on WhatsApp"
            )
        )

        val itemIds = campaignDao.insertItems(items)
        assertEquals(2, itemIds.size)

        val fetchedCampaign = campaignDao.getCampaignById(campaignId)
        assertNotNull(fetchedCampaign)
        assertEquals("Flash Sale Weekend", fetchedCampaign?.title)

        val fetchedItems = campaignDao.getItemsForCampaign(campaignId)
        assertEquals(2, fetchedItems.size)
        assertEquals("SENT", fetchedItems[0].status)
        assertEquals("FAILED", fetchedItems[1].status)
        assertEquals("Number not registered on WhatsApp", fetchedItems[1].errorMessage)
    }

    @Test
    fun `get campaign with items relation`() = runTest {
        val campaignId = campaignDao.insertCampaign(
            CampaignEntity(title = "VIP Broadcast", totalRecipients = 1)
        )
        campaignDao.insertItems(
            listOf(
                CampaignItemEntity(
                    campaignId = campaignId,
                    recipientPhone = "+6289999999",
                    recipientName = "Dewi",
                    messageText = "Exclusive Promo",
                    status = "SENT"
                )
            )
        )

        val result = campaignDao.getCampaignWithItems(campaignId)
        assertNotNull(result)
        assertEquals("VIP Broadcast", result?.campaign?.title)
        assertEquals(1, result?.items?.size)
        assertEquals("Dewi", result?.items?.first()?.recipientName)
    }

    @Test
    fun `delete campaign removes campaign and cascaded items`() = runTest {
        val cId = campaignDao.insertCampaign(
            CampaignEntity(title = "To be deleted", totalRecipients = 1)
        )
        campaignDao.insertItems(
            listOf(
                CampaignItemEntity(
                    campaignId = cId,
                    recipientPhone = "123",
                    recipientName = "Test",
                    messageText = "Msg",
                    status = "SENT"
                )
            )
        )

        assertEquals(1, campaignDao.getItemsForCampaign(cId).size)

        campaignDao.deleteCampaign(cId)
        assertNull(campaignDao.getCampaignById(cId))
        assertEquals(0, campaignDao.getItemsForCampaign(cId).size)
    }

    @Test
    fun `update campaign progress and telemetry`() = runTest {
        val campaignId = campaignDao.insertCampaign(
            CampaignEntity(
                title = "Newsletter Q4",
                totalRecipients = 50,
                status = "RUNNING"
            )
        )

        val initial = campaignDao.getCampaignById(campaignId)!!
        val updated = initial.copy(
            sentCount = 48,
            failedCount = 2,
            status = "COMPLETED",
            durationSeconds = 125L
        )

        campaignDao.updateCampaign(updated)

        val retrieved = campaignDao.getCampaignById(campaignId)!!
        assertEquals(48, retrieved.sentCount)
        assertEquals(2, retrieved.failedCount)
        assertEquals("COMPLETED", retrieved.status)
        assertEquals(125L, retrieved.durationSeconds)
    }

    @Test
    fun `observe all campaigns reactively and recent limits`() = runTest {
        for (i in 1..25) {
            campaignDao.insertCampaign(
                CampaignEntity(
                    title = "Blast #$i",
                    totalRecipients = i,
                    createdAt = 1000L * i
                )
            )
        }

        val all = campaignDao.observeAllCampaigns().first()
        assertEquals(25, all.size)
        assertEquals("Blast #25", all[0].title)

        val recent = campaignDao.getRecentCampaigns(10)
        assertEquals(10, recent.size)
        assertEquals("Blast #25", recent[0].title)
        assertEquals("Blast #16", recent[9].title)
    }

    @Test
    fun `database migration constant definition`() {
        assertEquals(1, AppDatabase.MIGRATION_1_2.startVersion)
        assertEquals(2, AppDatabase.MIGRATION_1_2.endVersion)
    }
}
