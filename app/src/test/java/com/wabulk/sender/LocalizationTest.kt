package com.wabulk.sender

import com.wabulk.sender.data.locale.AppLanguage
import com.wabulk.sender.data.locale.IndonesianStrings
import com.wabulk.sender.data.locale.LocalizationManager
import com.wabulk.sender.data.locale.TurkishStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalizationTest {

    @Test
    fun testLanguageSelection() {
        val manager = LocalizationManager(null)
        assertEquals(AppLanguage.INDONESIAN, manager.currentLanguage.value)
        assertEquals("Buat Pesan", manager.getStrings().navCompose)

        manager.setLanguage(AppLanguage.TURKISH)
        assertEquals(AppLanguage.TURKISH, manager.currentLanguage.value)
        assertEquals("Mesaj Oluştur", manager.getStrings().navCompose)
    }

    @Test
    fun testStringsCompleteness() {
        // Ensure Indonesian strings are non-empty
        assertNotNull(IndonesianStrings.composerTitle)
        assertNotNull(IndonesianStrings.contactsTitle)
        assertNotNull(IndonesianStrings.queueTitle)
        assertNotNull(IndonesianStrings.settingsTitle)

        // Ensure Turkish strings are non-empty
        assertNotNull(TurkishStrings.composerTitle)
        assertNotNull(TurkishStrings.contactsTitle)
        assertNotNull(TurkishStrings.queueTitle)
        assertNotNull(TurkishStrings.settingsTitle)

        // Check greeting formatting with variable
        val greetingId = IndonesianStrings.contactsGreetingPreview("Budi")
        assertTrue(greetingId.contains("Budi"))

        val greetingTr = TurkishStrings.contactsGreetingPreview("Ahmet")
        assertTrue(greetingTr.contains("Ahmet"))
    }
}
