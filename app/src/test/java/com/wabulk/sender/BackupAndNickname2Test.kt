package com.wabulk.sender

import com.wabulk.sender.data.io.BackupContact
import com.wabulk.sender.data.io.BackupManager
import com.wabulk.sender.data.io.KirimrasaBackup
import com.wabulk.sender.data.media.MediaAttachmentManager
import com.wabulk.sender.data.media.VideoCompressor
import com.wabulk.sender.data.local.db.ContactEntity
import com.wabulk.sender.domain.spintax.SpintaxParser
import com.wabulk.sender.update.UpdateManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupAndNickname2Test {

    @Test
    fun `nickname2 is used where the template asks for it`() {
        val out = SpintaxParser.interpolateVariables(
            "Halo {Nickname}, apa kabar {Nickname2}?", "Mas Budi", mapOf(SpintaxParser.NICKNAME2 to "Bud")
        )
        assertEquals("Halo Mas Budi, apa kabar Bud?", out)
    }

    @Test
    fun `empty nickname2 falls back to the first nickname`() {
        val out = SpintaxParser.interpolateVariables("{Nickname2} dan {Nickname}", "Rina", mapOf(SpintaxParser.NICKNAME2 to " "))
        assertEquals("Rina dan Rina", out)
    }

    @Test
    fun `backup round trip keeps every contact field`() {
        val contact = ContactEntity(
            id = 7, systemLookupKey = "lk", rawName = "Budi Santoso", phoneNumber = "6281234",
            customNickname = "Mas Budi", customNickname2 = "Bud", company = "PT A", custom1 = "X",
            tagsJson = "[\"VIP\",\"Kantor\"]"
        )
        val text = BackupManager.encode(KirimrasaBackup(contacts = listOf(BackupManager.toBackup(contact))))
        val back = BackupManager.decode(text)!!.contacts.single()
        assertEquals(BackupContact("Budi Santoso", "6281234", "Mas Budi", "Bud", "PT A", "X", listOf("VIP", "Kantor"), "lk"), back)
    }

    @Test
    fun `decode rejects files that are not Kirimrasa backups`() {
        assertNull(BackupManager.decode("""{"app":"other"}"""))
        assertNull(BackupManager.decode("nama,telepon\nBudi,0812"))
    }

    @Test
    fun `merge keeps id and selection, fills blanks, and unions labels`() {
        val existing = ContactEntity(
            id = 3, rawName = "Budi", phoneNumber = "6281234", customNickname = "Budi",
            company = "Lama", tagsJson = "[\"Kantor\"]", isSelected = true
        )
        val merged = BackupManager.merge(existing, BackupContact("Budi", "6281234", nickname = "Mas Budi", nickname2 = "Bud", tags = listOf("VIP", "Kantor")))
        assertEquals(3L, merged.id)
        assertTrue(merged.isSelected)
        assertEquals("Mas Budi", merged.customNickname)
        assertEquals("Bud", merged.customNickname2)
        assertEquals("Lama", merged.company)
        assertEquals("[\"Kantor\",\"VIP\"]", merged.tagsJson)
    }

    @Test
    fun `server key never goes into a backup`() {
        val prefs = BackupManager.snapshotPrefs("baileys_config_prefs", mapOf("api_key" to "secret", "gateway_url" to "https://x", "timeout_seconds" to 45L))
        assertFalse("api_key" in prefs)
        assertEquals("https://x", prefs["gateway_url"]!!.value)
        assertEquals("l", prefs["timeout_seconds"]!!.type)
    }

    @Test
    fun `only debug-signed installs moving to the release key must reinstall`() {
        assertTrue(UpdateManager.needsReinstall("v2.0.0", "1.2.8"))
        assertFalse(UpdateManager.needsReinstall("v1.2.9", "1.2.8"))
        assertFalse(UpdateManager.needsReinstall("v2.0.1", "2.0.0"))
    }

    @Test
    fun `big videos and documents are refused, big photos are recompressed instead`() {
        val mb = 1024L * 1024L
        assertTrue(MediaAttachmentManager.isTooBig("video/mp4", 80 * mb))
        assertTrue(MediaAttachmentManager.isTooBig("application/pdf", 17 * mb))
        assertFalse(MediaAttachmentManager.isTooBig("video/mp4", 16 * mb))
        assertFalse(MediaAttachmentManager.isTooBig("image/jpeg", 80 * mb))
    }

    @Test
    fun `video plan fits 15 MB, keeps the aspect ratio and refuses very long videos`() {
        val oneMinute = VideoCompressor.plan(60_000, 1920, 1080)!!
        assertEquals(1280, oneMinute.width)
        assertEquals(720, oneMinute.height)
        assertTrue(oneMinute.videoBps in 1_000_000..2_100_000)
        val portrait = VideoCompressor.plan(240_000, 1080, 1920)!!
        assertEquals(480, portrait.width) // low bitrate drops to 480p
        assertEquals(854, portrait.height)
        assertNull(VideoCompressor.plan(30 * 60_000, 1920, 1080))
        assertEquals(360, VideoCompressor.plan(10_000, 640, 360)!!.height) // never enlarged
    }
}
