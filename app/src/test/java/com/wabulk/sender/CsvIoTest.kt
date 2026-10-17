package com.wabulk.sender

import com.wabulk.sender.data.io.CampaignCsvExporter
import com.wabulk.sender.data.io.CsvContactParser
import com.wabulk.sender.data.io.CsvParseResult
import com.wabulk.sender.data.io.ParsedContact
import com.wabulk.sender.data.local.db.CampaignEntity
import com.wabulk.sender.data.local.db.CampaignItemEntity
import com.wabulk.sender.data.local.db.CampaignItemStatus
import com.wabulk.sender.data.local.db.CampaignStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class CsvIoTest {

    @Test
    fun `parseCsv parses standard comma-separated contacts with English headers`() {
        val csv = """
            Name,Phone,Nickname,Company,Custom 1,Tags
            Alice Smith,+1-555-0100,Ali,Acme Corp,VIP Client,"leads, vip"
            Bob Jones,(555) 010-9999,Bobby,Beta Ltd,Promo 50,customers
        """.trimIndent()

        val result = CsvContactParser.parseCsv(csv)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success

        assertEquals(2, success.rowCount)
        assertEquals(0, success.skippedCount)
        assertEquals(2, success.contacts.size)

        val c1 = success.contacts[0]
        assertEquals("Alice Smith", c1.rawName)
        assertEquals("+15550100", c1.phoneNumber)
        assertEquals("Ali", c1.customNickname)
        assertEquals("Acme Corp", c1.company)
        assertEquals("VIP Client", c1.custom1)
        assertEquals(listOf("leads", "vip"), c1.tags)

        val c2 = success.contacts[1]
        assertEquals("Bob Jones", c2.rawName)
        assertEquals("5550109999", c2.phoneNumber)
        assertEquals("Bobby", c2.customNickname)
        assertEquals("Beta Ltd", c2.company)
        assertEquals("Promo 50", c2.custom1)
        assertEquals(listOf("customers"), c2.tags)
    }

    @Test
    fun `parseCsv parses semicolon-separated contacts with Indonesian headers`() {
        val csv = """
            Nama;No HP;Panggilan;Perusahaan;Kustom 1;Kategori
            Budi Santoso;0812-3456-7890;Budi;PT Maju Bersama;Diskon 20%;Retail; Mitra
            Siti Aminah;+62 813 9876 5432;Siti;CV Berkah;Grosir;VIP
        """.trimIndent()

        val result = CsvContactParser.parseCsv(csv)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success

        assertEquals(2, success.rowCount)
        assertEquals(0, success.skippedCount)
        assertEquals(2, success.contacts.size)

        val c1 = success.contacts[0]
        assertEquals("Budi Santoso", c1.rawName)
        assertEquals("081234567890", c1.phoneNumber)
        assertEquals("Budi", c1.customNickname)
        assertEquals("PT Maju Bersama", c1.company)
        assertEquals("Diskon 20%", c1.custom1)
        assertEquals(listOf("Retail", "Mitra"), c1.tags)

        val c2 = success.contacts[1]
        assertEquals("Siti Aminah", c2.rawName)
        assertEquals("+6281398765432", c2.phoneNumber)
        assertEquals("Siti", c2.customNickname)
        assertEquals("CV Berkah", c2.company)
    }

    @Test
    fun `parseCsv parses contacts with Turkish headers`() {
        val csv = """
            İsim,Telefon,Takma Ad,Şirket,Özel 1,Etiket
            Ahmet Yılmaz,+90 532 111 2233,Ahmet,Anadolu A.Ş.,Öncelikli,Bayi
            Mehmet Demir,0542 333 4455,Memo,Demir Ltd,Kampanya,Müşteri
        """.trimIndent()

        val result = CsvContactParser.parseCsv(csv)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success

        assertEquals(2, success.contacts.size)
        assertEquals("Ahmet Yılmaz", success.contacts[0].rawName)
        assertEquals("+905321112233", success.contacts[0].phoneNumber)
        assertEquals("Ahmet", success.contacts[0].customNickname)
        assertEquals("Anadolu A.Ş.", success.contacts[0].company)
        assertEquals("Öncelikli", success.contacts[0].custom1)
        assertEquals(listOf("Bayi"), success.contacts[0].tags)
    }

    @Test
    fun `parseCsv handles RFC 4180 quotes, escaped quotes and multiline records`() {
        val csv = "Name,Phone,Company,Custom 1\n" +
                "\"Smith, John\",+1234567890,\"Acme, Inc.\",\"He said \"\"Special\"\" price\"\n" +
                "\"Multiline\nUser\",+9876543210,\"Line 1\nLine 2\",Standard\n"

        val result = CsvContactParser.parseCsv(csv)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success

        assertEquals(2, success.contacts.size)
        val c1 = success.contacts[0]
        assertEquals("Smith, John", c1.rawName)
        assertEquals("+1234567890", c1.phoneNumber)
        assertEquals("Acme, Inc.", c1.company)
        assertEquals("He said \"Special\" price", c1.custom1)

        val c2 = success.contacts[1]
        assertEquals("Multiline\nUser", c2.rawName)
        assertEquals("+9876543210", c2.phoneNumber)
        assertEquals("Line 1\nLine 2", c2.company)
    }

    @Test
    fun `parseCsv handles InputStream with UTF-8 BOM`() {
        val rawCsv = "\uFEFFFull Name,Mobile,Call Name\nCarlos Diaz,+34 600 000 000,Carlitos\n"
        val stream = ByteArrayInputStream(rawCsv.toByteArray(StandardCharsets.UTF_8))

        val result = CsvContactParser.parseCsv(stream)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success

        assertEquals(1, success.contacts.size)
        assertEquals("Carlos Diaz", success.contacts[0].rawName)
        assertEquals("+34600000000", success.contacts[0].phoneNumber)
        assertEquals("Carlitos", success.contacts[0].customNickname)
    }

    @Test
    fun `parseCsv returns error on empty input or missing phone header`() {
        val emptyResult = CsvContactParser.parseCsv("   \n\n  ")
        assertTrue(emptyResult is CsvParseResult.Error)

        val missingPhoneCsv = """
            Name,Company,Nickname
            John Doe,Acme,Johnny
        """.trimIndent()
        val missingPhoneResult = CsvContactParser.parseCsv(missingPhoneCsv)
        assertTrue(missingPhoneResult is CsvParseResult.Error)
    }

    @Test
    fun `parseCsv skips rows with empty or blank phone number`() {
        val csv = """
            Name,Phone,Company
            Valid User 1,+628111111,Company A
            Invalid User,,Company B
            Valid User 2,+628222222,Company C
            Spaces Only,   ,Company D
        """.trimIndent()

        val result = CsvContactParser.parseCsv(csv)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success

        assertEquals(4, success.rowCount)
        assertEquals(2, success.skippedCount)
        assertEquals(2, success.contacts.size)
        assertEquals("Valid User 1", success.contacts[0].rawName)
        assertEquals("Valid User 2", success.contacts[1].rawName)
    }

    @Test
    fun `CampaignCsvExporter generates valid RFC 4180 report with escaped content`() {
        val campaign = CampaignEntity(
            id = 42L,
            title = "Promo \"Ramadan\", 2026",
            totalRecipients = 2,
            sentCount = 1,
            failedCount = 1,
            status = CampaignStatus.COMPLETED.name
        )

        val items = listOf(
            CampaignItemEntity(
                id = 101L,
                campaignId = 42L,
                recipientPhone = "+628123456789",
                recipientName = "Budi Santoso",
                messageText = "Halo Budi!\nAda promo menarik untukmu.\nKunjungi web kami.",
                status = CampaignItemStatus.SENT.name,
                errorMessage = null,
                sentAt = 1770000000000L
            ),
            CampaignItemEntity(
                id = 102L,
                campaignId = 42L,
                recipientPhone = "+628987654321",
                recipientName = "Doe, Jane",
                messageText = "Hi Jane! \"Discount\" 50%",
                status = CampaignItemStatus.FAILED.name,
                errorMessage = "Network timeout: Connection reset, please retry",
                sentAt = 1770000005000L
            )
        )

        val csvOutput = CampaignCsvExporter.generateCsv(campaign, items)
        assertNotNull(csvOutput)

        val lines = csvOutput.lines().filter { it.isNotBlank() }
        assertEquals(3, lines.size)

        // Verify header
        assertEquals(
            "Campaign ID,Campaign Title,Recipient Phone,Recipient Name,Status,Error Message,Sent At Timestamp,Message Preview",
            lines[0]
        )

        // Line 1: Check campaign title quoted because of quotes and comma: "Promo ""Ramadan"", 2026"
        assertTrue(lines[1].contains("\"Promo \"\"Ramadan\"\", 2026\""))
        assertTrue(lines[1].contains("+628123456789"))
        assertTrue(lines[1].contains("Budi Santoso"))
        assertTrue(lines[1].contains("SENT"))
        // Check message preview line breaks replaced
        assertFalse(lines[1].contains("\nAda promo"))
        assertTrue(lines[1].contains("Halo Budi! Ada promo menarik untukmu. Kunjungi web kami."))

        // Line 2: Check comma in Recipient Name "Doe, Jane" and Error Message quoted
        assertTrue(lines[2].contains("\"Doe, Jane\""))
        assertTrue(lines[2].contains("FAILED"))
        assertTrue(lines[2].contains("\"Network timeout: Connection reset, please retry\""))
        assertTrue(lines[2].contains("\"Hi Jane! \"\"Discount\"\" 50%\""))
    }

    @Test
    fun `parseCsv auto-detects tab-separated values`() {
        val tsv = "Full Name\tPhone Number\tCompany\n" +
                "Kiki Siregar\t+62 855 123 456\tStartup Studio\n" +
                "Rudi Hartono\t0817-000-111\tBadminton Inc\n"

        val result = CsvContactParser.parseCsv(tsv)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success

        assertEquals(2, success.contacts.size)
        assertEquals("Kiki Siregar", success.contacts[0].rawName)
        assertEquals("+62855123456", success.contacts[0].phoneNumber)
        assertEquals("Startup Studio", success.contacts[0].company)
        assertEquals("Rudi Hartono", success.contacts[1].rawName)
        assertEquals("0817000111", success.contacts[1].phoneNumber)
    }

    @Test
    fun `parseCsv handles quoted tags containing delimiter in semicolon file`() {
        val csv = """
            Nama;No HP;Panggilan;Tags
            Andi;08123456789;Andi;"VIP; Partner; Reseller"
        """.trimIndent()

        val result = CsvContactParser.parseCsv(csv)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success

        assertEquals(1, success.contacts.size)
        assertEquals(listOf("VIP", "Partner", "Reseller"), success.contacts[0].tags)
    }

    @Test
    fun `parseCsv handles header-only CSV gracefully`() {
        val csv = "Name,Phone,Company\n"
        val result = CsvContactParser.parseCsv(csv)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success

        assertEquals(0, success.rowCount)
        assertEquals(0, success.skippedCount)
        assertEquals(0, success.contacts.size)
    }

    @Test
    fun `ParsedContact toContactEntity converts correctly`() {
        val parsed = ParsedContact(
            rawName = "Dewi Lestari",
            phoneNumber = "+6281234567890",
            customNickname = "Dewi",
            company = "Publishing Co",
            custom1 = "Writer",
            tags = listOf("author", "vip")
        )

        val entity = parsed.toContactEntity()
        assertEquals("Dewi Lestari", entity.rawName)
        assertEquals("+6281234567890", entity.phoneNumber)
        assertEquals("Dewi", entity.customNickname)
        assertEquals("[\"author\",\"vip\"]", entity.tagsJson)
    }

    @Test
    fun `parseCsv prioritizes Nomor HP over sequence column Nomor`() {
        val csv = """
            Nomor,Nama,Nomor HP
            1,Budi Santoso,08123456789
            2,Siti Aminah,08198765432
        """.trimIndent()

        val result = CsvContactParser.parseCsv(csv)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success

        assertEquals(2, success.contacts.size)
        assertEquals("08123456789", success.contacts[0].phoneNumber)
        assertEquals("Budi Santoso", success.contacts[0].rawName)
        assertEquals("08198765432", success.contacts[1].phoneNumber)
        assertEquals("Siti Aminah", success.contacts[1].rawName)
    }

    @Test
    fun `parseCsv handles bracketed template variable headers`() {
        val csv = """
            {Name},{Phone},{Company},{Custom_1}
            Alice Smith,+15550100,Acme Corp,PromoVIP
        """.trimIndent()

        val result = CsvContactParser.parseCsv(csv)
        assertTrue(result is CsvParseResult.Success)
        val success = result as CsvParseResult.Success

        assertEquals(1, success.contacts.size)
        assertEquals("Alice Smith", success.contacts[0].rawName)
        assertEquals("+15550100", success.contacts[0].phoneNumber)
        assertEquals("Acme Corp", success.contacts[0].company)
        assertEquals("PromoVIP", success.contacts[0].custom1)
    }

    @Test
    fun `parseCsv recognizes extended Turkish and Indonesian phone and name headers`() {
        val csvIndo = """
            Nama,Nomor Telepon
            Joko,0812111111
        """.trimIndent()
        val resIndo = CsvContactParser.parseCsv(csvIndo)
        assertTrue(resIndo is CsvParseResult.Success)
        assertEquals("0812111111", (resIndo as CsvParseResult.Success).contacts[0].phoneNumber)

        val csvTurk = """
            Ad Soyad,Cep Telefonu
            Murat Kaya,+905551234567
        """.trimIndent()
        val resTurk = CsvContactParser.parseCsv(csvTurk)
        assertTrue(resTurk is CsvParseResult.Success)
        val contact = (resTurk as CsvParseResult.Success).contacts[0]
        assertEquals("Murat Kaya", contact.rawName)
        assertEquals("+905551234567", contact.phoneNumber)
    }
}
