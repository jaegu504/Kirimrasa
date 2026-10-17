package com.wabulk.sender.data.locale

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalizationManager(context: Context? = null) {

    private val prefs = context?.getSharedPreferences("wa_bulk_locale_prefs", Context.MODE_PRIVATE)

    private val _currentLanguage = MutableStateFlow(
        prefs?.getString("pref_language_code", null)?.let { AppLanguage.fromCode(it) } ?: AppLanguage.INDONESIAN
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        prefs?.edit()?.putString("pref_language_code", language.code)?.apply()
    }

    fun getStrings(language: AppLanguage = _currentLanguage.value): AppStrings = when (language) {
        AppLanguage.INDONESIAN -> IndonesianStrings
        AppLanguage.TURKISH -> TurkishStrings
    }
}
