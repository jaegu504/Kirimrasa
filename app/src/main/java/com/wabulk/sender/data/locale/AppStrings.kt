package com.wabulk.sender.data.locale

import androidx.compose.runtime.compositionLocalOf

interface AppStrings {
    // Navigation Tabs
    val navCompose: String
    val navContacts: String
    val navQueue: String
    val navSettings: String

    // Top Header & Meta
    val appTitle: String
    val livePulseStatus: String
    val antiBanLevel3: String
    val campaignTagDraft: String

    // Screen 1: Composer
    val composerTitle: String
    val composerAccountPersonal: String
    val composerAccountBusiness: String
    val composerAccountDual: String
    val composerPrimaryNode: String
    val composerMediaQueue: String
    val composerAutoCompressed: String
    val composerAttachedCount: (Int) -> String
    val composerAttachButton: String
    val composerSpintaxTitle: String
    val composerSpintaxTapToInsert: String
    val composerPlaceholder: String
    val composerSpintaxValidated: String
    val composerCharCount: (Int, Int) -> String
    val composerAiRewriteBtn: String
    val composerToneFriendly: String
    val composerToneShorter: String
    val composerToneMaxVariance: String
    val composerLivePreviewTitle: String
    val composerOnlinePreview: String
    val composerDynamicJitterTitle: String
    val composerSafeDelayRange: String
    val composerJitterFast: String
    val composerJitterMetaCompliant: String
    val composerJitterRelaxed: String
    val composerLaunchBtn: String
    val composerLaunchContactsCount: (Int) -> String
    val composerCompanyVariableTitle: String
    val composerCompanyPlaceholder: String
    val composerCustom1VariableTitle: String
    val composerCustom1Placeholder: String
    val composerCustomVariablesDesc: String
    val composerSetVariablesBtn: String
    val composerBubbleNumbered: (Int) -> String
    val composerRemoveBubble: String
    val composerAddBubbleBtn: String
    val composerAiGenerating: String
    val composerMediaPickerError: (String) -> String
    val composerDualSyncLabel: String

    // Screen 2: Contacts
    val contactsTitle: String
    val contactsSubtitle: String
    val contactsSyncBtn: String
    val contactsSyncedAgo: String
    val contactsTotalMetric: String
    val contactsSelectedMetric: String
    val contactsNicknamesMetric: String
    val contactsSearchPlaceholder: String
    val contactsTagAll: (Int) -> String
    val contactsTagSelected: (Int) -> String
    val contactsTagFamily: (Int) -> String
    val contactsTagFriends: (Int) -> String
    val contactsTagBusiness: (Int) -> String
    val contactsNewTagBtn: String
    val contactsSelectAllInTag: (String, Int) -> String
    val contactsBulkAssignTagBtn: String
    val contactsCallNameLabel: String
    val contactsGreetingPreview: (String) -> String
    val contactsQuickFormat: String
    val contactsFormatFirstName: String
    val contactsFormatRemoveNotes: String
    val contactsFormatAddHello: String
    val contactsImportCsvBtn: String
    val contactsImportDialogTitle: String
    val contactsImportPickFile: String
    val contactsImportLoadSample: String
    val contactsImportResult: (Int, Int) -> String
    val contactsSyncing: String
    val contactsSelectAllInView: String
    val contactsDeselectAllInView: String
    val contactsAllSelectedInViewCount: (Int) -> String
    val contactsSelectAllInViewCount: (Int) -> String
    val contactsClearSearch: String
    val contactsAutoKak: String
    val contactsManageTagsTitle: String
    val contactsManageTagsDesc: String
    val contactsAddNewTagTitle: String
    val contactsAddNewTagDesc: String
    val contactsTagNamePlaceholder: String
    val contactsCancelBtn: String
    val contactsSaveBtn: String
    val contactsCloseBtn: String
    val contactsAddNewTagOption: String
    val contactsAddBtn: String
    val contactsDoneBtn: String
    val contactsBatchSelectedCount: (Int) -> String
    val contactsBatchAssignTagBtn: String
    val contactsBatchTagDialogTitle: (Int) -> String
    val contactsBatchTagAppliedToast: (Int, String) -> String
    val contactsViewSelectedOnly: String
    val contactsViewAll: String

    // Screen 3: Queue
    val queueTitle: String
    val queueCampaignName: String
    val queueMsgPerMin: String
    val queueDualNodeActive: String
    val queueSafeBadge: String
    val queueSentCount: (Int, Int) -> String
    val queueCompletePercentage: (Int) -> String
    val queueRecipientsRemaining: (Int) -> String
    val queueEstTimeLeft: String
    val queueWaitingJitter: (Int) -> String
    val queueTypingCadenceActive: String
    val queueMetricSuccess: String
    val queueMetricAvgDelay: String
    val queueMetricFlagAlert: String
    val queuePauseBtn: String
    val queueStopBtn: String
    val queueExportCsvBtn: String
    val queueExportSuccessToast: (Int) -> String
    val queueExportFailed: (String) -> String
    val queueFilterAll: String
    val queueFilterActive: String
    val queueFilterDelivered: String
    val queueFilterPending: String

    // Screen 4: Settings & Safety
    val settingsTitle: String
    val settingsSubtitle: String
    val settingsShieldActive: String
    val settingsGuardrailVersion: String
    val settingsEnginesTitle: String
    val settingsEnginesCount: String
    val settingsNode1Title: String
    val settingsNode1NodeNum: String
    val settingsConnected: String
    val settingsUptime: String
    val settingsHealth: String
    val settingsTestPingBtn: String
    val settingsPinging: String
    val settingsPongResult: String
    val settingsDisconnectBtn: String
    val settingsNode2Title: String
    val settingsNode2NodeNum: String
    val settingsPairingReady: String
    val settingsPairingDescription: String
    val settingsExpiresIn: String
    val settingsCopyOpenBtn: String
    val settingsCodeCopied: String
    val settingsSwitchToQr: String
    val settingsAntiBanTitle: String
    val settingsRecommended: String
    val settingsJitterTitle: String
    val settingsJitterSubtitle: String
    val settingsPresetConservative: String
    val settingsPresetOptimal: String
    val settingsPresetTurbo: String
    val settingsTypingSimTitle: String
    val settingsTypingSimSubtitle: String
    val settingsAutoRotateTitle: String
    val settingsAutoRotateSubtitle: String
    val settingsAutoRotatePattern: String
    val settingsCooldownTitle: String
    val settingsCooldownSubtitle: String
    val settingsPauseDuration: String
    val settingsBatchSize: String
    val settingsCooldownNotice: (Int) -> String
    val settingsAiToneTitle: String
    val settingsSpintaxEngineTag: String
    val settingsToneCasual: String
    val settingsToneFriendlyPicker: String
    val settingsToneFormal: String
    val settingsAiToneDesc: String
    val settingsBaileysTitle: String
    val settingsBaileysSubtitle: String
    val settingsBaileysToggle: String
    val settingsBaileysUrlLabel: String
    val settingsBaileysKeyLabel: String
    val settingsBaileysTestBtn: String
    val settingsLanguageTitle: String
    val settingsLanguageSubtitle: String
    val settingsSelectIndonesian: String
    val settingsSelectTurkish: String
    val settingsWaPersonalDetected: String
    val settingsWaPersonalNotDetected: String
    val settingsWaBusinessDetected: String
    val settingsWaBusinessNotDetected: String
    val settingsReadyToUse: String
    val settingsNotAvailable: String
    val settingsYourWaNumber: String
    val settingsYourWbNumber: String
    val settingsInputPhonePlaceholder: String
    val settingsOpenWaBtn: String
    val settingsOpenWbBtn: String
    val settingsCheckingStatus: String
    val settingsReadyStatus: String
    val settingsFailedStatus: String
    val settingsTestReadinessBtn: String
    val settingsA11yServiceTitle: String
    val settingsA11yServiceDesc: String
    val settingsA11yActiveBadge: String
    val settingsA11yInactiveBadge: String
    val settingsA11yActiveDesc: String
    val settingsA11yInactiveDesc: String
    val settingsA11yOpenSettingsBtn: String
    val settingsA11yActivateNowBtn: String
    val settingsStatusActive: String
    val settingsStatusInactive: String
    val settingsBaileysExplanation: String
    val settingsThemeTitle: String
    val settingsThemeSubtitle: String
    val settingsThemeDark: String
    val settingsThemeLight: String
    val settingsThemeSystem: String
    val settingsNode1PhoneLabel: String
    val settingsNode2PhoneLabel: String
    val settingsPairingCodeBtn: String

    val tutorialTitle: String
    val tutorialStep1Title: String
    val tutorialStep1Desc: String
    val tutorialStep2Title: String
    val tutorialStep2Desc: String
    val tutorialStep3Title: String
    val tutorialStep3Desc: String
    val tutorialStep4Title: String
    val tutorialStep4Desc: String
    val tutorialNextBtn: String
    val tutorialStartBtn: String
    val tutorialSkipBtn: String
    val settingsReopenTutorialBtn: String
}

object IndonesianStrings : AppStrings {
    override val navCompose = "Buat Pesan"
    override val navContacts = "Kontak"
    override val navQueue = "Antrean"
    override val navSettings = "Pengaturan"

    override val appTitle = "Kirimrasa"
    override val livePulseStatus = "Aktif"
    override val antiBanLevel3 = "Anti-Ban: Level 3"
    override val campaignTagDraft = "#104 • Draf"

    override val composerTitle = "Peracik Pesan"
    override val composerAccountPersonal = "WA Personal"
    override val composerAccountBusiness = "WA Bisnis"
    override val composerAccountDual = "Sinkron Ganda"
    override val composerPrimaryNode = "(Node Utama)"
    override val composerMediaQueue = "Antrean Media"
    override val composerAutoCompressed = "(Kompres Otomatis)"
    override val composerAttachedCount = { count: Int -> "$count Terlampir" }
    override val composerAttachButton = "+ Lampirkan"
    override val composerSpintaxTitle = "Variabel Dinamis Spintax"
    override val composerSpintaxTapToInsert = "Ketuk untuk menyisipkan"
    override val composerPlaceholder = "Tulis pesan siaran personal dengan dukungan spintax..."
    override val composerSpintaxValidated = "Spintax Tervalidasi"
    override val composerCharCount = { chars: Int, segments: Int -> "$chars karakter • $segments Segmen WA" }
    override val composerAiRewriteBtn = "Tulis Ulang AI & Anti-Spam Spin"
    override val composerToneFriendly = "Ramah"
    override val composerToneShorter = "Ringkas"
    override val composerToneMaxVariance = "Variasi Maksimum"
    override val composerLivePreviewTitle = "PRATINJAU LANGSUNG"
    override val composerOnlinePreview = "Pratinjau Online"
    override val composerDynamicJitterTitle = "Jeda Acak Dinamis"
    override val composerSafeDelayRange = "Jeda Aman: 25s - 60s"
    override val composerJitterFast = "Cepat (Agresif)"
    override val composerJitterMetaCompliant = "Sesuai Standar • Waktu Acak"
    override val composerJitterRelaxed = "Santai"
    override val composerLaunchBtn = "Mulai Kirim Massal"
    override val composerLaunchContactsCount = { count: Int -> "$count Kontak" }
    override val composerCompanyVariableTitle = "Nilai {Company} (Nama Bisnis / Brand):"
    override val composerCompanyPlaceholder = "Misal: Kirimrasa / Toko Berkah"
    override val composerCustom1VariableTitle = "Nilai {Custom_1} (Kode Promo / Link / Info Bebas):"
    override val composerCustom1Placeholder = "Misal: PROMO30 / https://wa.me/..."
    override val composerCustomVariablesDesc = "{Nickname} diambil otomatis dari nama panggilan kontak masing-masing di Tab Kontak."
    override val composerSetVariablesBtn = "Atur Nilai {Company} & {Custom_1}"
    override val composerBubbleNumbered = { num: Int -> "Balon #$num" }
    override val composerRemoveBubble = "Hapus Balon"
    override val composerAddBubbleBtn = "+ Tambah Balon Pesan (Maks 3)"
    override val composerAiGenerating = "Menghasilkan variasi AI & Spintax..."
    override val composerMediaPickerError = { err: String -> "Gagal membuka pemilih berkas: $err" }
    override val composerDualSyncLabel = "Sinkron Ganda"

    override val contactsTitle = "Pengelola Kontak"
    override val contactsSubtitle = "Sinkronisasi Buku Telepon & Personalisasi"
    override val contactsSyncBtn = "Sinkron"
    override val contactsSyncedAgo = "2m lalu"
    override val contactsTotalMetric = "Total"
    override val contactsSelectedMetric = "Terpilih"
    override val contactsNicknamesMetric = "Panggilan"
    override val contactsSearchPlaceholder = "Cari nama, nomor, atau tag..."
    override val contactsTagAll = { count: Int -> "Semua ($count)" }
    override val contactsTagSelected = { count: Int -> "Terpilih ($count)" }
    override val contactsTagFamily = { count: Int -> "Keluarga ($count)" }
    override val contactsTagFriends = { count: Int -> "Teman ($count)" }
    override val contactsTagBusiness = { count: Int -> "Bisnis ($count)" }
    override val contactsNewTagBtn = "Tag Baru"
    override val contactsSelectAllInTag = { tag: String, count: Int -> "Pilih Semua di $tag ($count)" }
    override val contactsBulkAssignTagBtn = "Beri Tag Massal"
    override val contactsCallNameLabel = "Panggilan / Nama Akrab"
    override val contactsGreetingPreview = { name: String -> "Pratinjau: \"Hai $name! 👋\"" }
    override val contactsQuickFormat = "Format cepat:"
    override val contactsFormatFirstName = "Ambil nama depan"
    override val contactsFormatRemoveNotes = "Hapus catatan kurung"
    override val contactsFormatAddHello = "+ Hai / Halo"
    override val contactsImportCsvBtn = "Impor CSV"
    override val contactsImportDialogTitle = "Impor Kontak dari CSV"
    override val contactsImportPickFile = "Pilih Berkas CSV dari Perangkat"
    override val contactsImportLoadSample = "Muat Contoh CSV (5 Kontak Demo)"
    override val contactsImportResult = { imported: Int, total: Int -> "Berhasil mengimpor $imported dari $total kontak" }
    override val contactsSyncing = "Menyinkronkan..."
    override val contactsSelectAllInView = "Pilih Semua"
    override val contactsDeselectAllInView = "Batal"
    override val contactsAllSelectedInViewCount = { count: Int -> "Semua Dipilih ($count Kontak)" }
    override val contactsSelectAllInViewCount = { count: Int -> "Pilih Semua Kontak ($count)" }
    override val contactsClearSearch = "Hapus pencarian"
    override val contactsAutoKak = "Otomatis 'Kak'"
    override val contactsManageTagsTitle = "Kelola Tag Kontak"
    override val contactsManageTagsDesc = "Pilih kategori tag yang ingin disematkan pada kontak ini:"
    override val contactsAddNewTagTitle = "Tambah Kategori Tag Baru"
    override val contactsAddNewTagDesc = "Buat tag kategori baru untuk mengelompokkan kontak (contoh: VIP, Pelanggan, Reseller, Klien)."
    override val contactsTagNamePlaceholder = "Nama tag (contoh: VIP)"
    override val contactsCancelBtn = "Batal"
    override val contactsSaveBtn = "Simpan"
    override val contactsCloseBtn = "Tutup"
    override val contactsAddNewTagOption = "+ Buat tag baru..."
    override val contactsAddBtn = "Tambah"
    override val contactsDoneBtn = "Selesai"
    override val contactsBatchSelectedCount = { count: Int -> "$count Terpilih" }
    override val contactsBatchAssignTagBtn = "+ Label"
    override val contactsBatchTagDialogTitle = { count: Int -> "Beri Label ke $count Kontak Terpilih" }
    override val contactsBatchTagAppliedToast = { count: Int, tag: String -> "$count kontak berhasil diberi label '$tag'" }
    override val contactsViewSelectedOnly = "Lihat Terpilih"
    override val contactsViewAll = "Lihat Semua"

    override val queueTitle = "Antrean Pengiriman Langsung"
    override val queueCampaignName = "Kampanye #104: Siaran Promo Musim Semi"
    override val queueMsgPerMin = "1.2 msg/menit"
    override val queueDualNodeActive = "Node Ganda WA • Sinkron Aktif"
    override val queueSafeBadge = "Aman Lv. 3"
    override val queueSentCount = { sent: Int, total: Int -> "$sent / $total Terkirim" }
    override val queueCompletePercentage = { pct: Int -> "$pct% Selesai" }
    override val queueRecipientsRemaining = { count: Int -> "$count Penerima Tersisa" }
    override val queueEstTimeLeft = "Perkiraan sisa 14m 10s"
    override val queueWaitingJitter = { sec: Int -> "Menunggu jeda anti-ban: ${sec}s" }
    override val queueTypingCadenceActive = "Ritme ketik manusiawi aktif"
    override val queueMetricSuccess = "Sukses"
    override val queueMetricAvgDelay = "Rata Jeda"
    override val queueMetricFlagAlert = "Peringatan"
    override val queuePauseBtn = "Jeda Antrean"
    override val queueStopBtn = "Hentikan Siaran"
    override val queueExportCsvBtn = "Ekspor Laporan CSV"
    override val queueExportSuccessToast = { count: Int -> "Laporan CSV berhasil diekspor ($count riwayat)" }
    override val queueExportFailed = { err: String -> "Gagal mengekspor laporan: $err" }
    override val queueFilterAll = "Semua"
    override val queueFilterActive = "Aktif"
    override val queueFilterDelivered = "Terkirim"
    override val queueFilterPending = "Menunggu"

    override val settingsTitle = "Pengaturan & Keamanan Akun"
    override val settingsSubtitle = "Algoritma anti-ban, routing multi-node & simulasi ritme manusiawi"
    override val settingsShieldActive = "Perisai Level 3 Aktif"
    override val settingsGuardrailVersion = "Guardrail v4.2"
    override val settingsEnginesTitle = "Engine Terhubung"
    override val settingsEnginesCount = "2 Node Terkonfigurasi"
    override val settingsNode1Title = "WhatsApp Personal"
    override val settingsNode1NodeNum = "Node #1"
    override val settingsConnected = "Terhubung"
    override val settingsUptime = "Waktu Aktif:"
    override val settingsHealth = "Kesehatan:"
    override val settingsTestPingBtn = "Uji Ping"
    override val settingsPinging = "Mengirim Ping (18ms)..."
    override val settingsPongResult = "Pong! 18ms"
    override val settingsDisconnectBtn = "Putuskan"
    override val settingsNode2Title = "WhatsApp Bisnis"
    override val settingsNode2NodeNum = "Node #2"
    override val settingsPairingReady = "Siap Ditautkan"
    override val settingsPairingDescription = "Tautkan instan di perangkat ini tanpa butuh HP kedua atau scan QR."
    override val settingsExpiresIn = "Kedaluwarsa dalam"
    override val settingsCopyOpenBtn = "Salin Kode & Buka WhatsApp"
    override val settingsCodeCopied = "Kode Disalin ke Clipboard!"
    override val settingsSwitchToQr = "Atau beralih ke scan QR standar"
    override val settingsAntiBanTitle = "Humanizer Anti-Ban"
    override val settingsRecommended = "Direkomendasikan"
    override val settingsJitterTitle = "Rentang Jeda Acak (Jitter)"
    override val settingsJitterSubtitle = "Jeda organik antar pengiriman paket"
    override val settingsPresetConservative = "Konservatif (35-75s)"
    override val settingsPresetOptimal = "Optimal (25-65s)"
    override val settingsPresetTurbo = "Turbo (15-30s)"
    override val settingsTypingSimTitle = "Simulasi Indikator Mengetik"
    override val settingsTypingSimSubtitle = "Siarkan status 'sedang mengetik' WhatsApp standar sebelum kirim"
    override val settingsAutoRotateTitle = "Rotasi Engine Otomatis"
    override val settingsAutoRotateSubtitle = "Bagi beban pengiriman pesan sama rata antara node terhubung (50%/50%)"
    override val settingsAutoRotatePattern = "Pola aktif: Bergantian Node #1 ➔ Node #2"
    override val settingsCooldownTitle = "Istirahat Jeda Kelompok (Cooldown)"
    override val settingsCooldownSubtitle = "Jeda hening berkala untuk menghindari heuristik deteksi spam otomatis"
    override val settingsPauseDuration = "Durasi Jeda"
    override val settingsBatchSize = "Ukuran Tiap Kelompok"
    override val settingsCooldownNotice = { count: Int -> "Engine otomatis istirahat tenang setiap $count pesan terkirim" }
    override val settingsAiToneTitle = "Nada Penulisan AI Bawaan"
    override val settingsSpintaxEngineTag = "Mesin Spintax"
    override val settingsToneCasual = "Santai"
    override val settingsToneFriendlyPicker = "Ramah"
    override val settingsToneFormal = "Formal"
    override val settingsAiToneDesc = "Digunakan satu ketukan pada editor pesan untuk membuat variasi kalimat unik penembus filter anti-spam WhatsApp."
    override val settingsBaileysTitle = "Gateway Bridge Baileys"
    override val settingsBaileysSubtitle = "Gateway multi-device tanpa scan ulang"
    override val settingsBaileysToggle = "Gunakan Gateway Baileys"
    override val settingsBaileysUrlLabel = "URL GATEWAY"
    override val settingsBaileysKeyLabel = "API KEY (OPSIONAL)"
    override val settingsBaileysTestBtn = "Uji Koneksi Gateway"
    override val settingsLanguageTitle = "Pengaturan Bahasa (Language)"
    override val settingsLanguageSubtitle = "Ganti bahasa antarmuka secara instan"
    override val settingsSelectIndonesian = "Bahasa Indonesia"
    override val settingsSelectTurkish = "Türkçe"
    override val settingsWaPersonalDetected = "Terdeteksi di HP (WhatsApp Personal)"
    override val settingsWaPersonalNotDetected = "Aplikasi WhatsApp Belum Terpasang"
    override val settingsWaBusinessDetected = "Terdeteksi di HP (WhatsApp Business)"
    override val settingsWaBusinessNotDetected = "Aplikasi WA Business Belum Terpasang"
    override val settingsReadyToUse = "Siap Pakai"
    override val settingsNotAvailable = "Belum Ada"
    override val settingsYourWaNumber = "Nomor WhatsApp Anda:"
    override val settingsYourWbNumber = "Nomor WhatsApp Business Anda:"
    override val settingsInputPhonePlaceholder = "Ketik nomor WhatsApp Anda (cth: +62 812-3456-7890)"
    override val settingsOpenWaBtn = "Buka WhatsApp"
    override val settingsOpenWbBtn = "Buka WA Business"
    override val settingsCheckingStatus = "Memeriksa..."
    override val settingsReadyStatus = "Siap Digunakan!"
    override val settingsFailedStatus = "Gagal"
    override val settingsTestReadinessBtn = "Uji Kesiapan"
    override val settingsA11yServiceTitle = "Layanan Aksesibilitas HP"
    override val settingsA11yServiceDesc = "Otomatisasi Klik Kirim Tanpa Server"
    override val settingsA11yActiveBadge = "Otomatis Aktif ✓"
    override val settingsA11yInactiveBadge = "Perlu Diaktifkan"
    override val settingsA11yActiveDesc = "Layanan Aksesibilitas aktif! Pesan antrean akan terbuka di WhatsApp dan tombol 'Kirim' akan ditekan otomatis tanpa Anda perlu mengetuk manual."
    override val settingsA11yInactiveDesc = "Agar pesan bulk dapat terkirim secara otomatis tanpa perlu server Baileys, aktifkan Layanan Aksesibilitas 'Kirimrasa' di pengaturan HP Anda."
    override val settingsA11yOpenSettingsBtn = "Buka Pengaturan Aksesibilitas"
    override val settingsA11yActivateNowBtn = "Aktifkan Layanan Aksesibilitas Sekarang"
    override val settingsStatusActive = "AKTIF"
    override val settingsStatusInactive = "NONAKTIF"
    override val settingsBaileysExplanation = "Gunakan gateway server Node.js Baileys lokal atau remote untuk pengiriman pesan massal berkecepatan tinggi dengan multi-device protocol."
    override val settingsThemeTitle = "Gaya Tema & Tampilan"
    override val settingsThemeSubtitle = "Pilih tampilan Glassmorphism Apple terang atau gelap"
    override val settingsThemeDark = "Mode Gelap (Apple Glass)"
    override val settingsThemeLight = "Mode Terang (Apple Glass)"
    override val settingsThemeSystem = "Ikuti Sistem"
    override val settingsNode1PhoneLabel = "WhatsApp Personal"
    override val settingsNode2PhoneLabel = "WhatsApp Business"
    override val settingsPairingCodeBtn = "Minta Kode Pairing 8-Digit"

    override val tutorialTitle = "Panduan & Tutorial Kirimrasa"
    override val tutorialStep1Title = "Selamat Datang di Kirimrasa"
    override val tutorialStep1Desc = "Aplikasi broadcast WhatsApp personal dengan gaya Glassmorphism modern, menjaga kehangatan pesan tanpa terkesan seperti bot."
    override val tutorialStep2Title = "Dua Mode Pengiriman Fleksibel"
    override val tutorialStep2Desc = "Pilih 'Gateway Multi-Device' untuk pengiriman massal di latar belakang tanpa scan ulang, atau gunakan 'Layanan Aksesibilitas' untuk otomatisasi langsung di HP tanpa server."
    override val tutorialStep3Title = "Variabel Personal & Anti-Spam"
    override val tutorialStep3Desc = "Gunakan tag {Nickname} untuk menyapa nama panggilan kontak, aktifkan Spintax untuk variasi kalimat otomatis, serta jeda aman agar nomor Anda tetap terlindungi."
    override val tutorialStep4Title = "Pengelolaan Kontak Cerdas"
    override val tutorialStep4Desc = "Pilih kontak cukup dengan sekali ketuk, gunakan segmen 'Terpilih', dan terapkan label secara serentak hanya dengan satu tombol."
    override val tutorialNextBtn = "Lanjut"
    override val tutorialStartBtn = "Mulai Gunakan Kirimrasa"
    override val tutorialSkipBtn = "Lewati"
    override val settingsReopenTutorialBtn = "Buka Panduan Tutorial"
}

object TurkishStrings : AppStrings {
    override val navCompose = "Mesaj Oluştur"
    override val navContacts = "Kişiler"
    override val navQueue = "Kuyruk"
    override val navSettings = "Ayarlar"

    override val appTitle = "Kirimrasa"
    override val livePulseStatus = "Aktif"
    override val antiBanLevel3 = "Anti-Ban: Seviye 3"
    override val campaignTagDraft = "#104 • Taslak"

    override val composerTitle = "Kampanya Oluşturucu"
    override val composerAccountPersonal = "WA Bireysel"
    override val composerAccountBusiness = "WA İşletme"
    override val composerAccountDual = "Çift Senkron"
    override val composerPrimaryNode = "(Birincil Düğüm)"
    override val composerMediaQueue = "Medya Kuyruğu"
    override val composerAutoCompressed = "(Otomatik Sıkıştırma)"
    override val composerAttachedCount = { count: Int -> "$count Eklendi" }
    override val composerAttachButton = "+ Ekle"
    override val composerSpintaxTitle = "Spintax Dinamik Değişkenleri"
    override val composerSpintaxTapToInsert = "Eklemek için dokunun"
    override val composerPlaceholder = "Spintax destekli kişiselleştirilmiş toplu mesajınızı yazın..."
    override val composerSpintaxValidated = "Spintax Doğrulandı"
    override val composerCharCount = { chars: Int, segments: Int -> "$chars karakter • $segments WA Segmenti" }
    override val composerAiRewriteBtn = "Yapay Zeka Yeniden Yazım & Anti-Spam Döngüsü"
    override val composerToneFriendly = "Samimi"
    override val composerToneShorter = "Kısa"
    override val composerToneMaxVariance = "Maksimum Varyans"
    override val composerLivePreviewTitle = "CANLI ÖNİZLEME"
    override val composerOnlinePreview = "Çevrimiçi Önizleme"
    override val composerDynamicJitterTitle = "Dinamik Gönderim Gecikmesi"
    override val composerSafeDelayRange = "Güvenli Aralık: 25sn - 60sn"
    override val composerJitterFast = "Hızlı (Agresif)"
    override val composerJitterMetaCompliant = "Meta Uyumlu • Rastgele Zamanlama"
    override val composerJitterRelaxed = "Rahat"
    override val composerLaunchBtn = "Toplu Yayını Başlat"
    override val composerLaunchContactsCount = { count: Int -> "$count Kişi" }
    override val composerCompanyVariableTitle = "{Company} Değeri (İşletme / Marka Adı):"
    override val composerCompanyPlaceholder = "Örnek: Kirimrasa / Butik Mağaza"
    override val composerCustom1VariableTitle = "{Custom_1} Değeri (Promosyon Kodu / Bağlantı):"
    override val composerCustom1Placeholder = "Örnek: INDIRIM30 / https://wa.me/..."
    override val composerCustomVariablesDesc = "{Nickname} değeri Kişiler sekmesindeki takma adlardan otomatik alınır."
    override val composerSetVariablesBtn = "{Company} & {Custom_1} Değerlerini Ayarla"
    override val composerBubbleNumbered = { num: Int -> "Mesaj Balonu #$num" }
    override val composerRemoveBubble = "Balonu Sil"
    override val composerAddBubbleBtn = "+ Mesaj Balonu Ekle (Maks 3)"
    override val composerAiGenerating = "Yapay zeka & Spintax varyantları üretiliyor..."
    override val composerMediaPickerError = { err: String -> "Dosya seçici açılamadı: $err" }
    override val composerDualSyncLabel = "Çift Senkronizasyon"

    override val contactsTitle = "Kişi Yöneticisi"
    override val contactsSubtitle = "Rehber Senkronizasyonu & Kişiselleştirme"
    override val contactsSyncBtn = "Senkronize Et"
    override val contactsSyncedAgo = "2dk önce"
    override val contactsTotalMetric = "Toplam"
    override val contactsSelectedMetric = "Seçilen"
    override val contactsNicknamesMetric = "Takma Ad"
    override val contactsSearchPlaceholder = "İsim, numara veya etiket ara..."
    override val contactsTagAll = { count: Int -> "Tümü ($count)" }
    override val contactsTagSelected = { count: Int -> "Seçilenler ($count)" }
    override val contactsTagFamily = { count: Int -> "Aile ($count)" }
    override val contactsTagFriends = { count: Int -> "Arkadaşlar ($count)" }
    override val contactsTagBusiness = { count: Int -> "İş ($count)" }
    override val contactsNewTagBtn = "Yeni Etiket"
    override val contactsSelectAllInTag = { tag: String, count: Int -> "$tag içindeki Tümünü Seç ($count)" }
    override val contactsBulkAssignTagBtn = "Toplu Etiket Ata"
    override val contactsCallNameLabel = "Hitap / Samimi Takma Ad"
    override val contactsGreetingPreview = { name: String -> "Önizleme: \"Merhaba $name! 👋\"" }
    override val contactsQuickFormat = "Hızlı format:"
    override val contactsFormatFirstName = "Sadece ilk ad"
    override val contactsFormatRemoveNotes = "Ek notları temizle"
    override val contactsFormatAddHello = "+ Merhaba / Selam"
    override val contactsImportCsvBtn = "CSV İçe Aktar"
    override val contactsImportDialogTitle = "CSV Kişilerini İçe Aktar"
    override val contactsImportPickFile = "Cihazdan CSV Dosyası Seç"
    override val contactsImportLoadSample = "Örnek CSV Yükle (5 Demo Kişi)"
    override val contactsImportResult = { imported: Int, total: Int -> "$total satırdan $imported kişi başarıyla içe aktarıldı" }
    override val contactsSyncing = "Senkronize ediliyor..."
    override val contactsSelectAllInView = "Tümünü Seç"
    override val contactsDeselectAllInView = "İptal"
    override val contactsAllSelectedInViewCount = { count: Int -> "Tümü Seçildi ($count Kişi)" }
    override val contactsSelectAllInViewCount = { count: Int -> "Tüm Kişileri Seç ($count)" }
    override val contactsClearSearch = "Aramayı temizle"
    override val contactsAutoKak = "Hitap Otomatik"
    override val contactsManageTagsTitle = "Kişi Etiketlerini Yönet"
    override val contactsManageTagsDesc = "Bu kişiye eklenecek etiket kategorilerini seçin:"
    override val contactsAddNewTagTitle = "Yeni Etiket Kategorisi Ekle"
    override val contactsAddNewTagDesc = "Bu kişileri gruplandırmak için yeni bir etiket kategorisi oluşturun (ör: VIP, Müşteri, Bayi, Aile)."
    override val contactsTagNamePlaceholder = "Etiket adı (ör: VIP)"
    override val contactsCancelBtn = "İptal"
    override val contactsSaveBtn = "Kaydet"
    override val contactsCloseBtn = "Kapat"
    override val contactsAddNewTagOption = "+ Yeni etiket oluştur..."
    override val contactsAddBtn = "Ekle"
    override val contactsDoneBtn = "Tamam"
    override val contactsBatchSelectedCount = { count: Int -> "$count Seçildi" }
    override val contactsBatchAssignTagBtn = "+ Etiket"
    override val contactsBatchTagDialogTitle = { count: Int -> "Seçilen $count Kişiye Etiket Ekle" }
    override val contactsBatchTagAppliedToast = { count: Int, tag: String -> "$count kişiye '$tag' etiketi uygulandı" }
    override val contactsViewSelectedOnly = "Sadece Seçilenler"
    override val contactsViewAll = "Tümünü Göster"

    override val queueTitle = "Canlı Gönderim Kuyruğu"
    override val queueCampaignName = "Kampanya #104: Sezon Promosyon Yayını"
    override val queueMsgPerMin = "1.2 mesaj/dk"
    override val queueDualNodeActive = "Çift WA Düğümü • Senkronizasyon Aktif"
    override val queueSafeBadge = "Güvenli Sv. 3"
    override val queueSentCount = { sent: Int, total: Int -> "$sent / $total Gönderildi" }
    override val queueCompletePercentage = { pct: Int -> "%$pct Tamamlandı" }
    override val queueRecipientsRemaining = { count: Int -> "$count Kalan Alıcı" }
    override val queueEstTimeLeft = "Tahmini 14dk 10sn kaldı"
    override val queueWaitingJitter = { sec: Int -> "Anti-ban gecikmesi bekleniyor: ${sec}sn" }
    override val queueTypingCadenceActive = "İnsansı yazma ritmi aktif"
    override val queueMetricSuccess = "Başarı"
    override val queueMetricAvgDelay = "Ort. Gecikme"
    override val queueMetricFlagAlert = "Uyarı"
    override val queuePauseBtn = "Kuyruğu Duraklat"
    override val queueStopBtn = "Yayını Durdur"
    override val queueExportCsvBtn = "CSV Raporunu Dışa Aktar"
    override val queueExportSuccessToast = { count: Int -> "CSV raporu başarıyla dışa aktarıldı ($count kayıt)" }
    override val queueExportFailed = { err: String -> "Rapor dışa aktarılamadı: $err" }
    override val queueFilterAll = "Tümü"
    override val queueFilterActive = "Aktif"
    override val queueFilterDelivered = "İletildi"
    override val queueFilterPending = "Bekliyor"

    override val settingsTitle = "Hesaplar ve Güvenlik Ayarları"
    override val settingsSubtitle = "Anti-ban algoritmaları, çoklu düğüm yönlendirmesi & insansı tempo"
    override val settingsShieldActive = "Seviye 3 Kalkan Aktif"
    override val settingsGuardrailVersion = "Guardrail v4.2"
    override val settingsEnginesTitle = "Bağlı Motorlar"
    override val settingsEnginesCount = "2 Düğüm Yapılandırıldı"
    override val settingsNode1Title = "WhatsApp Bireysel"
    override val settingsNode1NodeNum = "Düğüm #1"
    override val settingsConnected = "Bağlandı"
    override val settingsUptime = "Çalışma Süresi:"
    override val settingsHealth = "Sağlık:"
    override val settingsTestPingBtn = "Ping Testi"
    override val settingsPinging = "Ping Gönderiliyor (18ms)..."
    override val settingsPongResult = "Pong! 18ms"
    override val settingsDisconnectBtn = "Bağlantıyı Kes"
    override val settingsNode2Title = "WhatsApp İşletme"
    override val settingsNode2NodeNum = "Düğüm #2"
    override val settingsPairingReady = "Eşleşmeye Hazır"
    override val settingsPairingDescription = "İkinci bir telefona veya QR taramaya gerek kalmadan bu cihazda anında bağlayın."
    override val settingsExpiresIn = "Kalan süre"
    override val settingsCopyOpenBtn = "Kodu Kopyala & WhatsApp'ı Aç"
    override val settingsCodeCopied = "Kod Panoya Kopyalandı!"
    override val settingsSwitchToQr = "Veya standart QR taramasına geçin"
    override val settingsAntiBanTitle = "Anti-Ban İnsancıllaştırıcı"
    override val settingsRecommended = "Önerilen"
    override val settingsJitterTitle = "Rastgele Gecikme Aralığı (Jitter)"
    override val settingsJitterSubtitle = "Giden paketler arasında organik insansı duraklamalar"
    override val settingsPresetConservative = "Muhafazakar (35-75sn)"
    override val settingsPresetOptimal = "Optimal (25-65sn)"
    override val settingsPresetTurbo = "Turbo (15-30sn)"
    override val settingsTypingSimTitle = "Yazıyor Göstergesini Simüle Et"
    override val settingsTypingSimSubtitle = "Gönderimden önce standart WhatsApp yazıyor durumu yayınlar"
    override val settingsAutoRotateTitle = "Motorları Otomatik Döndür"
    override val settingsAutoRotateSubtitle = "Mesaj gönderimini bağlı düğümler arasında eşit olarak paylaştırır (%50/%50)"
    override val settingsAutoRotatePattern = "Aktif düzen: Düğüm #1 ➔ Düğüm #2 dönüşümü"
    override val settingsCooldownTitle = "Grup Dinlenme Molası (Cooldown)"
    override val settingsCooldownSubtitle = "Otomatik spam sezgisel tespitlerini önlemek için periyodik mola döngüleri"
    override val settingsPauseDuration = "Mola Süresi"
    override val settingsBatchSize = "Her Grup Boyutu"
    override val settingsCooldownNotice = { count: Int -> "Motor her $count giden mesajda otomatik olarak sessiz molaya girer" }
    override val settingsAiToneTitle = "Varsayılan Yapay Zeka Yeniden Yazım Tonu"
    override val settingsSpintaxEngineTag = "Spintax Motoru"
    override val settingsToneCasual = "Gündelik"
    override val settingsToneFriendlyPicker = "Samimi"
    override val settingsToneFormal = "Resmi"
    override val settingsAiToneDesc = "WhatsApp anti-spam eşiklerini geçen özgün sözdizimsel varyantlar üretmek için tek dokunuşla kullanılır."
    override val settingsBaileysTitle = "Baileys Köprü Ağ Geçidi"
    override val settingsBaileysSubtitle = "Tekrar tarama gerektirmeyen çoklu cihaz ağ geçidi"
    override val settingsBaileysToggle = "Baileys Ağ Geçidini Kullan"
    override val settingsBaileysUrlLabel = "AĞ GEÇİDİ URL'Sİ"
    override val settingsBaileysKeyLabel = "API ANAHTARI (İSTEĞE BAĞLI)"
    override val settingsBaileysTestBtn = "Ağ Geçidi Bağlantısını Test Et"
    override val settingsLanguageTitle = "Uygulama Dili (Dil Seçimi)"
    override val settingsLanguageSubtitle = "Arayüz dilini anında değiştirin"
    override val settingsSelectIndonesian = "Bahasa Indonesia"
    override val settingsSelectTurkish = "Türkçe"
    override val settingsWaPersonalDetected = "Cihazda Yüklü (WhatsApp Bireysel)"
    override val settingsWaPersonalNotDetected = "WhatsApp Uygulaması Yüklü Değil"
    override val settingsWaBusinessDetected = "Cihazda Yüklü (WhatsApp İşletme)"
    override val settingsWaBusinessNotDetected = "WhatsApp Business Yüklü Değil"
    override val settingsReadyToUse = "Kullanıma Hazır"
    override val settingsNotAvailable = "Mevcut Değil"
    override val settingsYourWaNumber = "WhatsApp Numaranız:"
    override val settingsYourWbNumber = "WhatsApp Business Numaranız:"
    override val settingsInputPhonePlaceholder = "WhatsApp numaranızı yazın (ör: +90 555 123 4567)"
    override val settingsOpenWaBtn = "WhatsApp'ı Aç"
    override val settingsOpenWbBtn = "WA Business'ı Aç"
    override val settingsCheckingStatus = "Kontrol ediliyor..."
    override val settingsReadyStatus = "Kullanıma Hazır!"
    override val settingsFailedStatus = "Başarısız"
    override val settingsTestReadinessBtn = "Hazırlık Testi"
    override val settingsA11yServiceTitle = "Cihaz Erişilebilirlik Hizmeti"
    override val settingsA11yServiceDesc = "Sunucusuz Otomatik Gönderim"
    override val settingsA11yActiveBadge = "Otomatik Aktif ✓"
    override val settingsA11yInactiveBadge = "Etkinleştirilmeli"
    override val settingsA11yActiveDesc = "Erişilebilirlik hizmeti aktif! Mesajlar WhatsApp'ta açılacak ve Gönder butonuna otomatik tıklanacaktır."
    override val settingsA11yInactiveDesc = "Toplu mesajların sunucusuz otomatik gönderilmesi için telefon ayarlarından 'Kirimrasa' Erişilebilirlik Hizmetini açın."
    override val settingsA11yOpenSettingsBtn = "Erişilebilirlik Ayarlarını Aç"
    override val settingsA11yActivateNowBtn = "Erişilebilirlik Hizmetini Şimdi Aç"
    override val settingsStatusActive = "AKTİF"
    override val settingsStatusInactive = "PASİF"
    override val settingsBaileysExplanation = "Çoklu cihaz protokolüyle yüksek hızlı toplu mesaj gönderimi için yerel veya uzak Baileys Node.js ağ geçidini kullanın."
    override val settingsThemeTitle = "Uygulama Teması (Görünüm)"
    override val settingsThemeSubtitle = "Açık veya koyu Apple Glassmorphism temasını seçin"
    override val settingsThemeDark = "Koyu Mod (Apple Glass)"
    override val settingsThemeLight = "Açık Mod (Apple Glass)"
    override val settingsThemeSystem = "Sistem Varsayılanı"
    override val settingsNode1PhoneLabel = "WhatsApp Bireysel"
    override val settingsNode2PhoneLabel = "WhatsApp İşletme"
    override val settingsPairingCodeBtn = "8 Haneli Eşleşme Kodu İste"

    override val tutorialTitle = "Kirimrasa Rehber & Eğitici"
    override val tutorialStep1Title = "Kirimrasa'ya Hoş Geldiniz"
    override val tutorialStep1Desc = "Modern Apple Glassmorphic tarzıyla, mesajlarınızın bot gibi değil sıcak ve samimi hissettirmesini sağlayan kişisel WhatsApp toplu mesajlaşma uygulaması."
    override val tutorialStep2Title = "İki Esnek Gönderim Modu"
    override val tutorialStep2Desc = "Tekrar tarama yapmadan arka planda toplu gönderim için 'Çoklu Cihaz Ağ Geçidi'ni seçin veya sunucu olmadan doğrudan cihaz içi otomasyon için 'Erişilebilirlik Hizmeti'ni kullanın."
    override val tutorialStep3Title = "Kişisel Değişkenler & Anti-Spam"
    override val tutorialStep3Desc = "Kişilere adlarıyla hitap etmek için {Nickname} etiketini kullanın, cümle varyasyonları için Spintax'ı açın ve numaranızı korumak için doğal insansı gecikmeler ekleyin."
    override val tutorialStep4Title = "Akıllı Kişi Yönetimi"
    override val tutorialStep4Desc = "Kişileri tek dokunuşla seçin, 'Seçilenler' sekmesinde görüntüleyin ve tek bir düğmeyle aynı anda etiketleyin."
    override val tutorialNextBtn = "İleri"
    override val tutorialStartBtn = "Kirimrasa'yı Başlat"
    override val tutorialSkipBtn = "Geç"
    override val settingsReopenTutorialBtn = "Kullanım Rehberini Aç"
}

val LocalAppStrings = compositionLocalOf<AppStrings> { IndonesianStrings }
