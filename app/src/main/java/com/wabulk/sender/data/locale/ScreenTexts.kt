package com.wabulk.sender.data.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Per-screen text objects (ComposerTexts, ContactsTexts, …) pick their Indonesian or Turkish
 * variant with this, so each screen's copy lives in its own file next to AppStrings.
 */
@Composable
@ReadOnlyComposable
fun isTurkishUi(): Boolean = LocalAppStrings.current === TurkishStrings

/** Same choice for non-Compose code (view models, services). */
fun isTurkishActive(): Boolean = LocalizationManager.activeLanguage == AppLanguage.TURKISH
