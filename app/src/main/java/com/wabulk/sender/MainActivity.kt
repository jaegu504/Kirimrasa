package com.wabulk.sender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.wabulk.sender.data.locale.LocalizationManager
import com.wabulk.sender.theme.EmeraldDarkSurface
import com.wabulk.sender.theme.WhatsAppBulkTheme
import com.wabulk.sender.ui.MainScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val localizationManager = LocalizationManager(applicationContext)
    com.wabulk.sender.engine.baileys.BaileysConfigManager.init(applicationContext)
    com.wabulk.sender.engine.WhatsAppSessionManager.getInstance(applicationContext)

    setContent {
      WhatsAppBulkTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = EmeraldDarkSurface
        ) {
          MainScreen(localizationManager = localizationManager)
        }
      }
    }
  }
}
