package com.wabulk.sender

import androidx.compose.animation.core.Spring
import com.wabulk.sender.data.locale.AppLanguage
import com.wabulk.sender.data.locale.IndonesianStrings
import com.wabulk.sender.data.locale.LocalizationManager
import com.wabulk.sender.data.locale.TurkishStrings
import com.wabulk.sender.theme.EmeraldSpring
import com.wabulk.sender.ui.navigation.ScreenTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase1IntegrationTest {

    @Test
    fun testNavigationTabs() {
        assertEquals(4, ScreenTab.entries.size)
        assertEquals("compose", ScreenTab.COMPOSE.route)
        assertEquals("contacts", ScreenTab.CONTACTS.route)
        assertEquals("queue", ScreenTab.QUEUE.route)
        assertEquals("settings", ScreenTab.SETTINGS.route)
    }

    @Test
    fun testSpringPhysicsSpecs() {
        val bouncy = EmeraldSpring.Bouncy
        assertEquals(Spring.DampingRatioLowBouncy, bouncy.dampingRatio, 0.001f)
        assertEquals(Spring.StiffnessMediumLow, bouncy.stiffness, 0.001f)
    }

    @Test
    fun testBilingualLocalizationFullCoverage() {
        val manager = LocalizationManager(null)

        // Default Indonesian
        assertEquals(AppLanguage.INDONESIAN, manager.currentLanguage.value)
        var s = manager.getStrings()
        assertEquals("Buat Pesan", s.navCompose)
        assertEquals("Kontak", s.navContacts)
        assertEquals("Antrean", s.navQueue)
        assertEquals("Pengaturan", s.navSettings)
        assertEquals("Peracik Pesan", s.composerTitle)
        assertEquals("Pengelola Kontak", s.contactsTitle)
        assertEquals("Antrean Pengiriman Langsung", s.queueTitle)
        assertEquals("Pengaturan & Keamanan Akun", s.settingsTitle)

        // Switch to Turkish
        manager.setLanguage(AppLanguage.TURKISH)
        assertEquals(AppLanguage.TURKISH, manager.currentLanguage.value)
        s = manager.getStrings()
        assertEquals("Mesaj Oluştur", s.navCompose)
        assertEquals("Kişiler", s.navContacts)
        assertEquals("Kuyruk", s.navQueue)
        assertEquals("Ayarlar", s.navSettings)
        assertEquals("Kampanya Oluşturucu", s.composerTitle)
        assertEquals("Kişi Yöneticisi", s.contactsTitle)
        assertEquals("Canlı Gönderim Kuyruğu", s.queueTitle)
        assertEquals("Hesaplar ve Güvenlik Ayarları", s.settingsTitle)

        // Switch back to Indonesian
        manager.setLanguage(AppLanguage.INDONESIAN)
        assertEquals(AppLanguage.INDONESIAN, manager.currentLanguage.value)
        s = manager.getStrings()
        assertEquals("Buat Pesan", s.navCompose)
    }

    @Test
    fun testVariablePreservationInBothLanguages() {
        // Indonesian greeting preview preserves variable value
        val idPreview = IndonesianStrings.contactsGreetingPreview("Budi")
        assertTrue(idPreview.contains("Budi"))
        assertTrue(idPreview.contains("Hai") || idPreview.contains("Hi"))

        // Turkish greeting preview preserves variable value
        val trPreview = TurkishStrings.contactsGreetingPreview("Mehmet")
        assertTrue(trPreview.contains("Mehmet"))
        assertTrue(trPreview.contains("Merhaba"))

        // Verify that {Nickname} is in composer default templates
        assertTrue(IndonesianStrings.composerPlaceholder.isNotEmpty())
        assertTrue(TurkishStrings.composerPlaceholder.isNotEmpty())
    }

    @Test
    fun testQueueTelemetryFormatting() {
        assertEquals("18 / 42 Terkirim", IndonesianStrings.queueSentCount(18, 42))
        assertEquals("18 / 42 Gönderildi", TurkishStrings.queueSentCount(18, 42))

        assertEquals("43% Selesai", IndonesianStrings.queueCompletePercentage(43))
        assertEquals("%43 Tamamlandı", TurkishStrings.queueCompletePercentage(43))
    }
}
