package com.wabulk.sender

import com.wabulk.sender.data.io.CampaignCsvExporter
import com.wabulk.sender.data.io.CsvContactParser
import com.wabulk.sender.data.io.CsvParseResult
import com.wabulk.sender.data.local.db.CampaignEntity
import com.wabulk.sender.data.local.db.CampaignItemEntity
import com.wabulk.sender.domain.model.MediaAttachment
import com.wabulk.sender.domain.model.MediaType
import com.wabulk.sender.engine.PingResult
import com.wabulk.sender.engine.baileys.BaileysBridgeClient
import com.wabulk.sender.engine.baileys.BaileysConfig
import com.wabulk.sender.engine.baileys.BaileysConfigManager
import com.wabulk.sender.engine.model.NodeSession
import com.wabulk.sender.domain.model.AccountNode
import com.wabulk.sender.engine.model.NodeStatus
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class UiEnhancementsTest {

    @Before
    fun setUp() {
        BaileysConfigManager.reset()
    }

    @After
    fun tearDown() {
        BaileysConfigManager.reset()
    }

    @Test
    fun testBaileysConfigStateManagement() {
        // Initial defaults
        val initialConfig = BaileysConfigManager.config.value
        assertEquals("http://10.0.2.2:3000", initialConfig.gatewayUrl)
        assertEquals("", initialConfig.apiKey)
        assertFalse(initialConfig.isEnabled)

        // Updates
        BaileysConfigManager.setGatewayUrl("http://192.168.1.100:4000")
        BaileysConfigManager.setApiKey("secret_token_xyz")
        BaileysConfigManager.setEnabled(true)

        val updatedConfig = BaileysConfigManager.config.value
        assertEquals("http://192.168.1.100:4000", updatedConfig.gatewayUrl)
        assertEquals("secret_token_xyz", updatedConfig.apiKey)
        assertTrue(updatedConfig.isEnabled)
    }

    @Test
    fun testSampleCsvImportDataIntegrity() {
        val sampleCsv = """
            Nama,No Telepon,Panggilan,Perusahaan,Custom_1,Tag
            Budi Santoso,+6281234567801,Budi,PT Maju Jaya,DISKON50,VIP
            Siti Nurhaliza,+6281234567802,Siti,Karya Abadi,FREEONGKIR,Bisnis
            Ahmad Fauzi,+6281234567803,Ahmad,Fauzi Tech,CASHBACK10,Klien
            Dewi Lestari,+6281234567804,Dewi,Bintang Mulia,MEMBERVIP,Keluarga
            Eko Prasetyo,+6281234567805,Eko,Prasetyo Store,HEMAT20,Teman
        """.trimIndent()

        val result = CsvContactParser.parseCsv(sampleCsv)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success
        assertEquals(5, success.contacts.size)
        assertEquals(5, success.rowCount)
        assertEquals(0, success.skippedCount)

        val first = success.contacts[0]
        assertEquals("Budi Santoso", first.rawName)
        assertEquals("+6281234567801", first.phoneNumber)
        assertEquals("Budi", first.customNickname)
        assertEquals("PT Maju Jaya", first.company)
        assertEquals("DISKON50", first.custom1)
        assertEquals(listOf("VIP"), first.tags)
    }

    @Test
    fun testCsvStreamParsing() {
        val csvData = "Name,Phone,Nickname\r\nAlice,08123456789,Ali\r\nBob,08987654321,Bobby"
        val inputStream = ByteArrayInputStream(csvData.toByteArray(StandardCharsets.UTF_8))

        val result = CsvContactParser.parseCsv(inputStream)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success
        assertEquals(2, success.contacts.size)
        assertEquals("08123456789", success.contacts[0].phoneNumber)
        assertEquals("Alice", success.contacts[0].rawName)
    }

    @Test
    fun testCampaignReportCsvGeneration() {
        val campaign = CampaignEntity(
            id = 42L,
            title = "Promo Gajian",
            totalRecipients = 2,
            sentCount = 2,
            failedCount = 0,
            status = "COMPLETED",
            createdAt = 1710000000000L
        )

        val items = listOf(
            CampaignItemEntity(
                id = 1L,
                campaignId = 42L,
                recipientPhone = "+62811111111",
                recipientName = "John Doe",
                messageText = "Halo John, promo khusus hari ini!",
                status = "DELIVERED",
                errorMessage = null,
                sentAt = 1710000010000L
            ),
            CampaignItemEntity(
                id = 2L,
                campaignId = 42L,
                recipientPhone = "+62822222222",
                recipientName = "Jane Roe",
                messageText = "Halo Jane, klaim diskonmu!",
                status = "FAILED",
                errorMessage = "Nomor tidak terdaftar",
                sentAt = 1710000020000L
            )
        )

        val csv = CampaignCsvExporter.generateCsv(campaign, items)
        assertTrue(csv.startsWith("Campaign ID,Campaign Title,Recipient Phone,Recipient Name,Status,Error Message,Sent At Timestamp,Message Preview"))
        assertTrue(csv.contains("42,Promo Gajian,+62811111111,John Doe,DELIVERED,,1710000010000,\"Halo John, promo khusus hari ini!\""))
        assertTrue(csv.contains("42,Promo Gajian,+62822222222,Jane Roe,FAILED,Nomor tidak terdaftar,1710000020000,\"Halo Jane, klaim diskonmu!\""))
    }

    @Test
    fun testMediaAttachmentPickerMimeTypeMapping() {
        val imgAttachment = MediaAttachment(
            uriString = "content://media/external/images/123",
            fileName = "flyer.png",
            mimeType = "image/png",
            sizeBytes = 204800L
        )
        assertEquals(MediaType.IMAGE, imgAttachment.mediaType)
        assertEquals("200 KB", imgAttachment.formattedSize)

        val videoAttachment = MediaAttachment(
            uriString = "content://media/external/video/456",
            fileName = "tutorial.mp4",
            mimeType = "video/mp4",
            sizeBytes = 5242880L
        )
        assertEquals(MediaType.VIDEO, videoAttachment.mediaType)
        assertEquals("5.0 MB", videoAttachment.formattedSize)

        val docAttachment = MediaAttachment(
            uriString = "content://com.android.providers.downloads/789",
            fileName = "proposal.pdf",
            mimeType = "application/pdf",
            sizeBytes = 1048576L
        )
        assertEquals(MediaType.DOCUMENT, docAttachment.mediaType)
        assertEquals("1.0 MB", docAttachment.formattedSize)
    }
}
