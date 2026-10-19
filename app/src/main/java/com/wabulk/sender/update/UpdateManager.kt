package com.wabulk.sender.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import com.wabulk.sender.service.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdateInfo(
    val latestVersion: String,
    val downloadUrl: String,
    val changelog: String,
    val apkSize: Long,
    val isUpdateAvailable: Boolean
)

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class Available(val info: AppUpdateInfo) : UpdateState()
    data class Downloading(val progressPercent: Int, val downloadedBytes: Long, val totalBytes: Long) : UpdateState()
    data class ReadyToInstall(val apkFile: File) : UpdateState()
    data class Error(val message: String) : UpdateState()
    object UpToDate : UpdateState()
}

object UpdateManager {
    const val CURRENT_VERSION = "1.1.1"
    const val GITHUB_REPO = "jaegu504/Kirimrasa"
    private const val GITHUB_API_URL = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private var downloadedApkFile: File? = null

    fun dismissDialog() {
        _showDialog.value = false
    }

    fun openDialog() {
        _showDialog.value = true
    }

    fun resetState() {
        _updateState.value = UpdateState.Idle
    }

    /**
     * Checks if remote version is newer than current version (e.g. "v1.1.2" vs "1.1.1").
     */
    fun isNewerVersion(remoteTag: String, currentVersion: String): Boolean {
        val cleanRemote = remoteTag.trim().removePrefix("v").removePrefix("V")
        val cleanCurrent = currentVersion.trim().removePrefix("v").removePrefix("V")

        val remoteParts = cleanRemote.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = cleanCurrent.split(".").mapNotNull { it.toIntOrNull() }

        val maxLen = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }

    /**
     * Queries GitHub Releases API for the latest published release.
     */
    fun checkForUpdates(context: Context, isManual: Boolean = false) {
        scope.launch {
            _updateState.value = UpdateState.Checking
            try {
                val updateInfo = withContext(Dispatchers.IO) {
                    val url = URL(GITHUB_API_URL)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.connectTimeout = 10000
                    conn.readTimeout = 10000
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                    conn.setRequestProperty("User-Agent", "Kirimrasa-Android-Client")

                    if (conn.responseCode !in 200..299) {
                        throw Exception("HTTP ${conn.responseCode}: ${conn.responseMessage}")
                    }

                    val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                    conn.disconnect()

                    val json = JSONObject(responseText)
                    val tagName = json.optString("tag_name", "")
                    val body = json.optString("body", "Pembaruan versi terbaru.")

                    // Find APK Asset
                    var apkUrl = ""
                    var apkSize = 0L
                    val assets = json.optJSONArray("assets")
                    if (assets != null) {
                        for (i in 0 until assets.length()) {
                            val asset = assets.getJSONObject(i)
                            val name = asset.optString("name", "")
                            if (name.endsWith(".apk", ignoreCase = true)) {
                                apkUrl = asset.optString("browser_download_url", "")
                                apkSize = asset.optLong("size", 0L)
                                break
                            }
                        }
                    }

                    val isAvailable = isNewerVersion(tagName, CURRENT_VERSION) && apkUrl.isNotBlank()
                    AppUpdateInfo(
                        latestVersion = tagName,
                        downloadUrl = apkUrl,
                        changelog = body,
                        apkSize = apkSize,
                        isUpdateAvailable = isAvailable
                    )
                }

                if (updateInfo.isUpdateAvailable) {
                    _updateState.value = UpdateState.Available(updateInfo)
                    _showDialog.value = true
                    NotificationHelper.showUpdateAvailableNotification(
                        context = context.applicationContext,
                        latestVersion = updateInfo.latestVersion,
                        changelog = updateInfo.changelog
                    )
                } else {
                    _updateState.value = if (isManual) UpdateState.UpToDate else UpdateState.Idle
                }
            } catch (e: Exception) {
                if (isManual) {
                    _updateState.value = UpdateState.Error(e.message ?: "Gagal memeriksa pembaruan")
                } else {
                    _updateState.value = UpdateState.Idle
                }
            }
        }
    }

    /**
     * Downloads APK with redirect following (GitHub Release -> S3) and installs it.
     */
    fun startDownloadAndInstall(context: Context, info: AppUpdateInfo) {
        scope.launch {
            _updateState.value = UpdateState.Downloading(0, 0L, info.apkSize)
            _showDialog.value = true

            try {
                val apkFile = withContext(Dispatchers.IO) {
                    val destDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        ?: context.cacheDir
                    if (!destDir.exists()) destDir.mkdirs()

                    val targetFile = File(destDir, "Kirimrasa-${info.latestVersion}.apk")
                    if (targetFile.exists()) targetFile.delete()

                    // Follow redirects manually (HTTP 302 -> AWS S3 CDN)
                    var currentUrl = info.downloadUrl
                    var conn: HttpURLConnection
                    var redirectCount = 0

                    while (true) {
                        conn = URL(currentUrl).openConnection() as HttpURLConnection
                        conn.instanceFollowRedirects = false
                        conn.connectTimeout = 15000
                        conn.readTimeout = 30000
                        conn.setRequestProperty("User-Agent", "Kirimrasa-Android-Client")

                        val status = conn.responseCode
                        if (status in 300..399) {
                            val newUrl = conn.getHeaderField("Location")
                            conn.disconnect()
                            currentUrl = newUrl ?: throw Exception("Redirect URL kosong")
                            redirectCount++
                            if (redirectCount > 5) throw Exception("Terlalu banyak pengalihan (redirect loop)")
                        } else if (status in 200..299) {
                            break
                        } else {
                            throw Exception("HTTP $status saat mengunduh APK")
                        }
                    }

                    val totalLength = if (conn.contentLengthLong > 0) conn.contentLengthLong else info.apkSize
                    var downloaded = 0L

                    conn.inputStream.use { input: InputStream ->
                        FileOutputStream(targetFile).use { output: FileOutputStream ->
                            val buffer = ByteArray(16 * 1024)
                            var bytesRead: Int
                            var lastPercent = 0

                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                downloaded += bytesRead
                                if (totalLength > 0) {
                                    val pct = ((downloaded * 100) / totalLength).toInt().coerceIn(0, 100)
                                    if (pct != lastPercent) {
                                        lastPercent = pct
                                        _updateState.value = UpdateState.Downloading(pct, downloaded, totalLength)
                                    }
                                }
                            }
                        }
                    }
                    conn.disconnect()
                    targetFile
                }

                downloadedApkFile = apkFile
                _updateState.value = UpdateState.ReadyToInstall(apkFile)

                // Trigger package installer immediately
                withContext(Dispatchers.Main) {
                    installApk(context, apkFile)
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error("Gagal mengunduh: ${e.message}")
            }
        }
    }

    /**
     * Checks unknown sources permission and triggers the Android Package Installer.
     */
    fun installApk(context: Context, file: File? = downloadedApkFile) {
        val target = file ?: downloadedApkFile ?: return
        if (!target.exists() || target.length() == 0L) {
            _updateState.value = UpdateState.Error("File APK tidak ditemukan atau rusak.")
            return
        }

        try {
            // Android 8.0+ unknown sources permission check
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        if (context !is Activity) {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    }
                    context.startActivity(settingsIntent)
                    return
                }
            }

            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                target
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(installIntent)
        } catch (e: Exception) {
            _updateState.value = UpdateState.Error("Gagal memasang APK: ${e.message}")
        }
    }
}
