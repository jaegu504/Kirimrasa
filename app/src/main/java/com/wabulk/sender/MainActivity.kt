package com.wabulk.sender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.wabulk.sender.data.locale.LocalizationManager
import com.wabulk.sender.ui.MainScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val localizationManager = LocalizationManager(applicationContext)
    com.wabulk.sender.theme.ThemeManager.init(applicationContext)
    com.wabulk.sender.engine.baileys.BaileysConfigManager.init(applicationContext)
    com.wabulk.sender.engine.queue.AntiBanSettings.init(applicationContext)
    com.wabulk.sender.data.LastMessageStore.init(applicationContext)
    com.wabulk.sender.engine.WhatsAppSessionManager.getInstance(applicationContext)

    if (intent?.getBooleanExtra("EXTRA_OPEN_UPDATE_DIALOG", false) == true) {
      com.wabulk.sender.update.UpdateManager.openDialog()
    }

    setContent {
      com.wabulk.sender.theme.KirimrasaTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
          MainScreen(localizationManager = localizationManager)
        }
      }
    }
  }

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    if (intent.getBooleanExtra("EXTRA_OPEN_UPDATE_DIALOG", false)) {
      com.wabulk.sender.update.UpdateManager.openDialog()
    }
  }
}
