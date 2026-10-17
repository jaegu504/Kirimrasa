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

        // New Task 4 UI enhancement strings
        assertNotNull(IndonesianStrings.settingsBaileysTitle)
        assertNotNull(IndonesianStrings.settingsBaileysSubtitle)
        assertNotNull(IndonesianStrings.settingsBaileysToggle)
        assertNotNull(IndonesianStrings.settingsBaileysUrlLabel)
        assertNotNull(IndonesianStrings.settingsBaileysKeyLabel)
        assertNotNull(IndonesianStrings.settingsBaileysTestBtn)
        assertNotNull(IndonesianStrings.contactsImportCsvBtn)
        assertNotNull(IndonesianStrings.contactsImportDialogTitle)
        assertNotNull(IndonesianStrings.contactsImportPickFile)
        assertNotNull(IndonesianStrings.contactsImportLoadSample)
        assertNotNull(IndonesianStrings.contactsImportResult(5, 5))
        assertNotNull(IndonesianStrings.queueExportCsvBtn)
        assertNotNull(IndonesianStrings.queueExportSuccessToast(10))

        assertNotNull(TurkishStrings.settingsBaileysTitle)
        assertNotNull(TurkishStrings.settingsBaileysSubtitle)
        assertNotNull(TurkishStrings.settingsBaileysToggle)
        assertNotNull(TurkishStrings.settingsBaileysUrlLabel)
        assertNotNull(TurkishStrings.settingsBaileysKeyLabel)
        assertNotNull(TurkishStrings.settingsBaileysTestBtn)
        assertNotNull(TurkishStrings.contactsImportCsvBtn)
        assertNotNull(TurkishStrings.contactsImportDialogTitle)
        assertNotNull(TurkishStrings.contactsImportPickFile)
        assertNotNull(TurkishStrings.contactsImportLoadSample)
        assertNotNull(TurkishStrings.contactsImportResult(5, 5))
        assertNotNull(TurkishStrings.queueExportCsvBtn)
        assertNotNull(TurkishStrings.queueExportSuccessToast(10))
    }
}
