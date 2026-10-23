package com.wabulk.sender.data.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/** Copy for the Settings tab. */
interface SettingsTexts {
    val title: String

    val sectionAccounts: String
    val personal: String
    val business: String
    val statusConnected: String
    val statusConnecting: String
    val statusNotLinked: String
    val statusFailed: String
    val link: String
    val phoneLabel: String
    val requestCode: String
    val requesting: String
    val yourCode: String
    val codeValidFor: (String) -> String
    val codeExpired: String
    val copyAndOpen: String
    val linkSteps: String
    val testReady: String
    val checking: String
    val ready: String
    val notReady: String
    val disconnect: String
    val disconnectTitle: (String) -> String
    val disconnectBody: String
    val cancel: String
    val appNotInstalled: String
    val serverOff: String

    val sectionSafety: String
    val speedTitle: String
    val delayCaption: (Int, Int) -> String
    val presetCareful: String
    val presetNormal: String
    val presetFast: String
    val presetOwn: String
    val showTyping: String
    val breakTitle: String
    val breakCaption: (Int, Int) -> String
    val breakLength: String
    val breakEvery: String
    val decrease: String
    val increase: String

    val sectionAi: String
    val toneTitle: String
    val toneCaption: String

    val sectionGeneral: String
    val language: String
    val appearance: String
    val themeAuto: String
    val themeLight: String
    val themeDark: String
    val update: String
    val updateCaption: String
    val guide: String
    val guideCaption: String

    val advanced: String
    val advancedCaption: String
    val server: String
    val useServer: String
    val serverCaption: String
    val serverAddress: String
    val accessKey: String
    val accessKeyHint: String
    val showKey: String
    val hideKey: String
    val testConnection: String
    val testing: String
}

object IndonesianSettingsTexts : SettingsTexts {
    override val title = "Pengaturan"

    override val sectionAccounts = "Akun WhatsApp"
    override val personal = "WhatsApp Pribadi"
    override val business = "WhatsApp Bisnis"
    override val statusConnected = "Tersambung"
    override val statusConnecting = "Menyambungkan…"
    override val statusNotLinked = "Belum ditautkan"
    override val statusFailed = "Gagal tersambung"
    override val link = "Tautkan"
    override val phoneLabel = "Nomor WhatsApp Anda"
    override val requestCode = "Minta kode 8 digit"
    override val requesting = "Meminta kode…"
    override val yourCode = "Kode Anda"
    override val codeValidFor = { time: String -> "Berlaku $time lagi" }
    override val codeExpired = "Kode sudah habis. Minta kode baru."
    override val copyAndOpen = "Salin kode & buka WhatsApp"
    override val linkSteps = "Di WhatsApp: Perangkat tertaut > Tautkan dengan nomor telepon, lalu tempel kode."
    override val testReady = "Tes kesiapan"
    override val checking = "Memeriksa…"
    override val ready = "Siap dipakai"
    override val notReady = "Belum siap"
    override val disconnect = "Putuskan"
    override val disconnectTitle = { name: String -> "Putuskan $name?" }
    override val disconnectBody = "Pesan tidak bisa dikirim dari akun ini sampai ditautkan lagi."
    override val cancel = "Batal"
    override val appNotInstalled = "Aplikasinya belum terpasang di HP ini."
    override val serverOff = "Server sedang mati. Nyalakan di Pengaturan lanjutan."

    override val sectionSafety = "Keamanan pengiriman"
    override val speedTitle = "Kecepatan kirim"
    override val delayCaption = { min: Int, max: Int -> "Jeda $min–$max detik antar kontak" }
    override val presetCareful = "Hati-hati"
    override val presetNormal = "Normal"
    override val presetFast = "Cepat"
    override val presetOwn = "Sendiri"
    override val showTyping = "Tampilkan \"sedang mengetik\""
    override val breakTitle = "Istirahat otomatis"
    override val breakCaption = { minutes: Int, every: Int -> "$minutes menit setiap $every pesan" }
    override val breakLength = "Lama istirahat"
    override val breakEvery = "Setiap"
    override val decrease = "Kurangi"
    override val increase = "Tambah"

    override val sectionAi = "AI"
    override val toneTitle = "Gaya tulisan AI"
    override val toneCaption = "Dipakai saat AI menulis ulang pesan Anda."

    override val sectionGeneral = "Umum"
    override val language = "Bahasa"
    override val appearance = "Tampilan"
    override val themeAuto = "Otomatis"
    override val themeLight = "Terang"
    override val themeDark = "Gelap"
    override val update = "Pembaruan aplikasi"
    override val updateCaption = "Ketuk untuk cek versi baru"
    override val guide = "Panduan"
    override val guideCaption = "Lihat lagi cara memakai aplikasi"

    override val advanced = "Pengaturan lanjutan"
    override val advancedCaption = "Hanya ubah jika diminta"
    override val server = "Server"
    override val useServer = "Kirim lewat server"
    override val serverCaption = "Biarkan menyala agar WhatsApp bisa ditautkan dan pesan terkirim."
    override val serverAddress = "Alamat server"
    override val accessKey = "Kunci akses"
    override val accessKeyHint = "Kosongkan jika tidak ada"
    override val showKey = "Tampilkan kunci"
    override val hideKey = "Sembunyikan kunci"
    override val testConnection = "Tes koneksi"
    override val testing = "Menguji…"
}

object TurkishSettingsTexts : SettingsTexts {
    override val title = "Ayarlar"

    override val sectionAccounts = "Hesaplar"
    override val personal = "Kişisel WhatsApp"
    override val business = "WhatsApp Business"
    override val statusConnected = "Bağlı"
    override val statusConnecting = "Bağlanıyor…"
    override val statusNotLinked = "Bağlı değil"
    override val statusFailed = "Bağlanamadı"
    override val link = "Bağla"
    override val phoneLabel = "WhatsApp numaranız"
    override val requestCode = "8 haneli eşleme kodu al"
    override val requesting = "Kod alınıyor…"
    override val yourCode = "Eşleme kodu"
    override val codeValidFor = { time: String -> "$time daha geçerli" }
    override val codeExpired = "Kodun süresi doldu. Yeni kod alın."
    override val copyAndOpen = "Kodu kopyala ve WhatsApp'ı aç"
    override val linkSteps = "WhatsApp'ta: Bağlı cihazlar > Telefon numarasıyla bağla, sonra kodu yapıştırın."
    override val testReady = "Hazır mı kontrol et"
    override val checking = "Kontrol ediliyor…"
    override val ready = "Kullanıma hazır"
    override val notReady = "Hazır değil"
    override val disconnect = "Bağlantıyı kes"
    override val disconnectTitle = { name: String -> "$name bağlantısı kesilsin mi?" }
    override val disconnectBody = "Yeniden bağlanana kadar bu hesaptan mesaj gönderilemez."
    override val cancel = "Vazgeç"
    override val appNotInstalled = "Bu uygulama telefonda yüklü değil."
    override val serverOff = "Sunucu kapalı. Gelişmiş ayarlardan açın."

    override val sectionSafety = "Gönderim güvenliği"
    override val speedTitle = "Gönderim hızı"
    override val delayCaption = { min: Int, max: Int -> "Kişiler arasında $min–$max sn bekleme" }
    override val presetCareful = "Dikkatli"
    override val presetNormal = "Normal"
    override val presetFast = "Hızlı"
    override val presetOwn = "Özel"
    override val showTyping = "\"Yazıyor...\" göster"
    override val breakTitle = "Otomatik mola"
    override val breakCaption = { minutes: Int, every: Int -> "Her $every mesajda $minutes dk mola" }
    override val breakLength = "Mola süresi"
    override val breakEvery = "Kaç mesajda bir"
    override val decrease = "Azalt"
    override val increase = "Artır"

    override val sectionAi = "Yapay zekâ"
    override val toneTitle = "Yazım tonu"
    override val toneCaption = "Yapay zekâ mesajınızı yeniden yazarken kullanılır."

    override val sectionGeneral = "Genel"
    override val language = "Dil"
    override val appearance = "Görünüm"
    override val themeAuto = "Otomatik"
    override val themeLight = "Açık"
    override val themeDark = "Koyu"
    override val update = "Güncelleme"
    override val updateCaption = "Yeni sürümü kontrol etmek için dokunun"
    override val guide = "Kullanım rehberi"
    override val guideCaption = "Uygulamanın nasıl kullanıldığını tekrar görün"

    override val advanced = "Gelişmiş ayarlar"
    override val advancedCaption = "Yalnızca istenirse değiştirin"
    override val server = "Sunucu"
    override val useServer = "Sunucu üzerinden gönder"
    override val serverCaption = "WhatsApp'ı bağlamak ve mesaj göndermek için açık bırakın."
    override val serverAddress = "Sunucu adresi"
    override val accessKey = "Erişim anahtarı"
    override val accessKeyHint = "Yoksa boş bırakın"
    override val showKey = "Anahtarı göster"
    override val hideKey = "Anahtarı gizle"
    override val testConnection = "Bağlantıyı test et"
    override val testing = "Test ediliyor…"
}

@Composable
@ReadOnlyComposable
fun settingsTexts(): SettingsTexts = if (isTurkishUi()) TurkishSettingsTexts else IndonesianSettingsTexts
