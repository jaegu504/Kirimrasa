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
    val settingsUpdateTitle: String
    val settingsUpdateDesc: String
    val settingsCheckUpdateBtn: String
    val settingsCheckingUpdates: String
    val settingsAppUpToDate: String

    // Extracted from hardcoded UI text
    val updateApkLabel: String
    val updateAvailableTitle: String
    val updateReleaseNotes: String
    val updateDefaultChangelog: String
    val updateNowBtn: String
    val updateLaterBtn: String
    val updateDownloading: String
    val updateDownloadHint: String
    val updateReadyTitle: String
    val updateReadyDesc: String
    val updateInstallBtn: String
    val updateDownloadFailedTitle: String
    val retryBtn: String
    val updateUpToDateTitle: String
    val updateUpToDateDesc: (String) -> String
    val updateCheckingTitle: String
    val updateCheckFailed: String
    val updateDownloadError: (String) -> String
    val updateApkMissing: String
    val updateInstallError: (String) -> String
    val notifSendingChannel: String
    val notifSendingChannelDesc: String
    val notifUpdateChannel: String
    val notifUpdateChannelDesc: String
    val notifUpdateTitle: (String) -> String
    val notifUpdateBody: (String, String) -> String
    val notifServiceRunning: String
    val notifServiceTitle: String
    val notifServiceContent: String
    val notifDoneTitle: String
    val notifDoneBody: (Int) -> String
    val notifTypingTitle: String
    val notifTypingBody: (String, Int, Int) -> String
    val notifSendingTitle: (Int) -> String
    val notifSendingBody: (Int, Int) -> String
    val queueReadyTitle: String
    val gatewayConnecting: String
    val gatewayNotLinked: String
    val gatewayAlreadyLinked: String
    val waOpenFailed: (String) -> String
    val waInstalled: String
    val waNotInstalled: String
    val csvReadFailed: (String) -> String
    val csvEmpty: String
    val csvNoRecords: String
    val csvMissingPhone: String
    val gatewayConnectFailed: String
    val codeCopied: (String) -> String
    val waNotFound: String
    val composerSampleMessage: String
    val aiConnectFailed: String
    val filePickerFailed: (String) -> String
    val allContacts: String
    val composerGuideTitle: String
    val composerGuide1: String
    val composerGuide2: String
    val composerGuide3: String
    val composerGuide4: String
    val syncResult: (Int, Int) -> String
    val contactsPermissionNeeded: String
    val failedWith: (String) -> String
    val csvImported: (Int) -> String
    val csvOpenFailed: String
    val csvImportTitle: String
    val queueStatePaused: String
    val queueStateSending: String
    val queueStateIdle: String
    val queueDone: String
    val queueTypingTo: (String) -> String
    val queuePausedBanner: String
    val queueWaitingSafeDelay: (Int) -> String
    val queueReadyToSend: String
    val queueEmpty: String
    val queueFilterEmpty: String
    val queueTypingBadge: String
    val campaignDefaultTitle: String
    val sendFailed: String
    val reportSubject: (String) -> String
    val reportExportChooser: String
    val reportExported: (Int) -> String
    val reportExportFailed: (String) -> String
    val tutorialMenuDesc: String
    val toggleThemeDesc: String
    val fallbackBroadcastMessage: String
    val waPersonalDetected: String
    val waPersonalMissing: String
    val readyLabel: String
    val notAvailableLabel: String
    val yourWaNumber: String
    val waNumberHint: String
    val waPersonalNotInstalledToast: String
    val openWhatsAppBtn: String
    val readyToUse: String
    val failedLabel: String
    val testReadinessBtn: String
    val waBusinessDetected: String
    val waBusinessMissing: String
    val yourWbNumber: String
    val wbNumberHint: String
    val waBusinessNotInstalledToast: String
    val openWaBusinessBtn: String
    val activeLabel: String
    val inactiveLabel: String
    val gatewayDesc: String
    val apiKeyHint: String
    val gatewayConnectedLatency: (Long) -> String
    val minutesShort: (Int) -> String
    val messagesShort: (Int) -> String
    val randomDelayLabel: String
    val qrLinkTitle: String
    val qrLinkDesc1: String
    val qrLinkDesc2: String
    val connectedViaGateway: (String) -> String
    val requestPairingCodeBtn: String
    val yourPairingCode: String
    val copyCodeOpenWaBtn: String
    val nicknameFallback: String
    val greetingWord: String
    val understoodBtn: String
    val roundRobinLabel: String
    val queueBadgeWaiting: String
    val queueBadgeFailed: String
    val queueBadgeSent: String
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
    override val tutorialStep2Title = "Pengiriman Cepat Multi-Device"
    override val tutorialStep2Desc = "Gunakan fitur 'Gateway Multi-Device' bawaan untuk pengiriman massal di latar belakang tanpa perlu scan berulang, menjaga koneksi tetap stabil dan aman."
    override val tutorialStep3Title = "Variabel Personal & Anti-Spam"
    override val tutorialStep3Desc = "Gunakan tag {Nickname} untuk menyapa nama panggilan kontak, aktifkan Spintax untuk variasi kalimat otomatis, serta jeda aman agar nomor Anda tetap terlindungi."
    override val tutorialStep4Title = "Pengelolaan Kontak Cerdas"
    override val tutorialStep4Desc = "Pilih kontak cukup dengan sekali ketuk, gunakan segmen 'Terpilih', dan terapkan label secara serentak hanya dengan satu tombol."
    override val tutorialNextBtn = "Lanjut"
    override val tutorialStartBtn = "Mulai Gunakan Kirimrasa"
    override val tutorialSkipBtn = "Lewati"
    override val settingsReopenTutorialBtn = "Buka Panduan Tutorial"
    override val settingsUpdateTitle = "Pembaruan Aplikasi"
    override val settingsUpdateDesc = "Periksa dan pasang versi terbaru Kirimrasa secara instan"
    override val settingsCheckUpdateBtn = "Periksa Pembaruan"
    override val settingsCheckingUpdates = "Memeriksa..."
    override val settingsAppUpToDate = "Aplikasi Anda sudah versi terbaru ✓"

    // Extracted from hardcoded UI text
    override val updateApkLabel: String = "APK Resmi"
    override val updateAvailableTitle: String = "Pembaruan Tersedia!"
    override val updateReleaseNotes: String = "Catatan Rilis:"
    override val updateDefaultChangelog: String = "Peningkatan performa dan kestabilan aplikasi."
    override val updateNowBtn: String = "Perbarui Sekarang"
    override val updateLaterBtn: String = "Nanti Saja"
    override val updateDownloading: String = "Mengunduh Pembaruan..."
    override val updateDownloadHint: String = "Aplikasi akan otomatis membuka layar pemasangan setelah selesai."
    override val updateReadyTitle: String = "Unduhan Siap Dipasang!"
    override val updateReadyDesc: String = "Ketuk tombol di bawah untuk menyelesaikan pemasangan versi terbaru."
    override val updateInstallBtn: String = "Pasang Pembaruan"
    override val updateDownloadFailedTitle: String = "Gagal Mengunduh"
    override val retryBtn: String = "Coba Lagi"
    override val updateUpToDateTitle: String = "Versi Terbaru!"
    override val updateUpToDateDesc: (String) -> String = { v -> "Kirimrasa v$v adalah versi paling mutakhir yang tersedia." }
    override val updateCheckingTitle: String = "Memeriksa Pembaruan..."
    override val updateCheckFailed: String = "Gagal memeriksa pembaruan"
    override val updateDownloadError: (String) -> String = { m -> "Gagal mengunduh: $m" }
    override val updateApkMissing: String = "File APK tidak ditemukan atau rusak."
    override val updateInstallError: (String) -> String = { m -> "Gagal memasang APK: $m" }
    override val notifSendingChannel: String = "Pengiriman Massal WhatsApp"
    override val notifSendingChannelDesc: String = "Notifikasi status berjalan untuk proses pengiriman pesan massal WhatsApp"
    override val notifUpdateChannel: String = "Pembaruan Kirimrasa"
    override val notifUpdateChannelDesc: String = "Notifikasi ketersediaan versi baru aplikasi Kirimrasa"
    override val notifUpdateTitle: (String) -> String = { v -> "Pembaruan Kirimrasa $v Tersedia!" }
    override val notifUpdateBody: (String, String) -> String = { v, s -> "Versi baru ($v) telah dirilis:\n$s\n\nKetuk untuk mengunduh dan memasang langsung." }
    override val notifServiceRunning: String = "Layanan pengiriman aktif..."
    override val notifServiceTitle: String = "Kirimrasa - Berjalan"
    override val notifServiceContent: String = "Menjalankan antrean pengiriman dengan pengaman anti-ban..."
    override val notifDoneTitle: String = "Pengiriman Selesai!"
    override val notifDoneBody: (Int) -> String = { n -> "Semua $n pesan berhasil terkirim." }
    override val notifTypingTitle: String = "Mengetik Pesan..."
    override val notifTypingBody: (String, Int, Int) -> String = { name, i, n -> "Mengetik pesan untuk $name ($i/$n)" }
    override val notifSendingTitle: (Int) -> String = { p -> "Mengirim Pesan ($p%)" }
    override val notifSendingBody: (Int, Int) -> String = { s, n -> "$s/$n terkirim - Lanjut ke pesan berikutnya" }
    override val queueReadyTitle: String = "Broadcast Siap Dimulai"
    override val gatewayConnecting: String = "Menyambungkan ke WhatsApp..."
    override val gatewayNotLinked: String = "WhatsApp belum terhubung (Minta kode pairing di Pengaturan)"
    override val gatewayAlreadyLinked: String = "WhatsApp sudah terhubung di perangkat ini"
    override val waOpenFailed: (String) -> String = { m -> "Gagal membuka WhatsApp: $m" }
    override val waInstalled: String = "WhatsApp Terpasang di HP"
    override val waNotInstalled: String = "Aplikasi WhatsApp belum terpasang di HP"
    override val csvReadFailed: (String) -> String = { m -> "Gagal membaca file CSV: $m" }
    override val csvEmpty: String = "File CSV kosong"
    override val csvNoRecords: String = "Tidak ada data kontak yang valid di CSV"
    override val csvMissingPhone: String = "Kolom nomor telepon tidak ditemukan di baris judul CSV"
    override val gatewayConnectFailed: String = "Koneksi gateway gagal"
    override val codeCopied: (String) -> String = { c -> "Kode $c disalin ke clipboard!" }
    override val waNotFound: String = "WhatsApp tidak ditemukan di perangkat ini"
    override val composerSampleMessage: String = "Halo {Nickname}! 👋\nKami dari {Company} ingin membagikan penawaran spesial minggu ini."
    override val aiConnectFailed: String = "Gagal terhubung ke layanan AI"
    override val filePickerFailed: (String) -> String = { m -> "Gagal membuka pemilih berkas: $m" }
    override val allContacts: String = "Semua Kontak"
    override val composerGuideTitle: String = "Panduan Pesan & Variabel"
    override val composerGuide1: String = "1. Spintax {A|B|C}: Memutar variasi kata secara otomatis agar tiap pesan ke penerima berbeda dan tidak terdeteksi bot/spam."
    override val composerGuide2: String = "2. {Nickname}: Nama panggilan unik yang otomatis diambil dari Tab Kontak."
    override val composerGuide3: String = "3. {Company} & {Custom_1}: Variabel pengirim dan promo yang dapat Anda ubah nilainya pada menu 'Atur Nilai {Company}'."
    override val composerGuide4: String = "4. Multi-Balon: Memecah pesan panjang menjadi 2-3 balon obrolan dengan jeda mengetik 3 detik untuk simulasi mengetik manusia."
    override val syncResult: (Int, Int) -> String = { a, b -> "+$a baru, $b diperbarui" }
    override val contactsPermissionNeeded: String = "Izin kontak diperlukan"
    override val failedWith: (String) -> String = { m -> "Gagal: $m" }
    override val csvImported: (Int) -> String = { n -> "+$n diimpor dari CSV" }
    override val csvOpenFailed: String = "Gagal membuka file CSV"
    override val csvImportTitle: String = "Impor CSV"
    override val queueStatePaused: String = "DIJEDA"
    override val queueStateSending: String = "MENGIRIM"
    override val queueStateIdle: String = "SIAGA"
    override val queueDone: String = "Selesai"
    override val queueTypingTo: (String) -> String = { n -> "Mengetik: $n" }
    override val queuePausedBanner: String = "Antrean sedang dijeda"
    override val queueWaitingSafeDelay: (Int) -> String = { s -> "Menunggu jeda aman (${s}d)" }
    override val queueReadyToSend: String = "Siap mengirim"
    override val queueEmpty: String = "Belum ada antrean broadcast. Susun pesan di tab 'Buat Pesan' dan tekan 'Mulai Kirim Massal' untuk memulai pengiriman."
    override val queueFilterEmpty: String = "Tidak ada kontak dalam filter ini"
    override val queueTypingBadge: String = "MENGETIK (2d)"
    override val campaignDefaultTitle: String = "Kampanye Pesan Massal WhatsApp"
    override val sendFailed: String = "Gagal terkirim"
    override val reportSubject: (String) -> String = { t -> "Laporan Kampanye $t" }
    override val reportExportChooser: String = "Ekspor Laporan CSV"
    override val reportExported: (Int) -> String = { n -> "Laporan CSV berhasil diekspor ($n data)" }
    override val reportExportFailed: (String) -> String = { m -> "Gagal mengekspor laporan: $m" }
    override val tutorialMenuDesc: String = "Panduan & Tutorial"
    override val toggleThemeDesc: String = "Ganti Tema"
    override val fallbackBroadcastMessage: String = "Halo {Nickname}, penawaran spesial minggu ini dari {Company}! Gunakan kode {Custom_1}."
    override val waPersonalDetected: String = "Terdeteksi di HP (WhatsApp Personal)"
    override val waPersonalMissing: String = "Aplikasi WhatsApp Belum Terpasang"
    override val readyLabel: String = "Siap Pakai"
    override val notAvailableLabel: String = "Belum Ada"
    override val yourWaNumber: String = "Nomor WhatsApp Anda:"
    override val waNumberHint: String = "Ketik nomor WhatsApp Anda (cth: +62 812-3456-7890)"
    override val waPersonalNotInstalledToast: String = "WhatsApp Personal tidak terpasang di HP"
    override val openWhatsAppBtn: String = "Buka WhatsApp"
    override val readyToUse: String = "Siap Digunakan!"
    override val failedLabel: String = "Gagal"
    override val testReadinessBtn: String = "Uji Kesiapan"
    override val waBusinessDetected: String = "Terdeteksi di HP (WhatsApp Business)"
    override val waBusinessMissing: String = "Aplikasi WA Business Belum Terpasang"
    override val yourWbNumber: String = "Nomor WhatsApp Business Anda:"
    override val wbNumberHint: String = "Ketik nomor WA Business Anda (cth: +62 812-3456-7890)"
    override val waBusinessNotInstalledToast: String = "WhatsApp Business tidak terpasang di HP"
    override val openWaBusinessBtn: String = "Buka WA Business"
    override val activeLabel: String = "AKTIF"
    override val inactiveLabel: String = "NONAKTIF"
    override val gatewayDesc: String = "Gunakan server gateway untuk mengirim pesan massal lewat WhatsApp yang sudah ditautkan."
    override val apiKeyHint: String = "Kunci API gateway (kosongkan jika tidak ada)"
    override val gatewayConnectedLatency: (Long) -> String = { ms -> "Terhubung (${ms}ms)" }
    override val minutesShort: (Int) -> String = { n -> "$n mnt" }
    override val messagesShort: (Int) -> String = { n -> "$n pesan" }
    override val randomDelayLabel: String = "Jeda Acak"
    override val qrLinkTitle: String = "Tautkan dengan Kode QR"
    override val qrLinkDesc1: String = "Mode 8-Digit Pairing Code lebih direkomendasikan karena dapat dilakukan langsung di HP yang sama tanpa perlu kamera ponsel lain."
    override val qrLinkDesc2: String = "Untuk menggunakan Kode Pairing: Klik 'Salin & Buka WhatsApp', lalu buka Pengaturan WA > Perangkat Tertaut > Tautkan dengan nomor telepon."
    override val connectedViaGateway: (String) -> String = { n -> "Terhubung via Gateway ($n)" }
    override val requestPairingCodeBtn: String = "Minta Kode Pairing 8-Digit"
    override val yourPairingCode: String = "Kode Pairing WhatsApp Anda:"
    override val copyCodeOpenWaBtn: String = "Salin Kode & Buka WhatsApp"
    override val nicknameFallback: String = "Kak"
    override val greetingWord: String = "Hai"
    override val understoodBtn: String = "Mengerti"
    override val roundRobinLabel: String = "Bergantian"
    override val queueBadgeWaiting: String = "MENUNGGU"
    override val queueBadgeFailed: String = "GAGAL"
    override val queueBadgeSent: String = "TERKIRIM"
}

object TurkishStrings : AppStrings {
    override val navCompose = "Mesaj Oluştur"
    override val navContacts = "Kişiler"
    override val navQueue = "Sıra"
    override val navSettings = "Ayarlar"

    override val appTitle = "Kirimrasa"
    override val livePulseStatus = "Aktif"
    override val antiBanLevel3 = "Ban Koruması: Seviye 3"
    override val campaignTagDraft = "#104 • Taslak"

    override val composerTitle = "Mesaj Hazırla"
    override val composerAccountPersonal = "WA Kişisel"
    override val composerAccountBusiness = "WA Business"
    override val composerAccountDual = "İki Hesap"
    override val composerPrimaryNode = "(Ana Hesap)"
    override val composerMediaQueue = "Eklenen Medya"
    override val composerAutoCompressed = "(Otomatik Sıkıştırma)"
    override val composerAttachedCount = { count: Int -> "$count dosya eklendi" }
    override val composerAttachButton = "+ Ekle"
    override val composerSpintaxTitle = "Değişkenler ve Spintax"
    override val composerSpintaxTapToInsert = "Eklemek için dokunun"
    override val composerPlaceholder = "Kişiye özel mesajınızı yazın (spintax desteklenir)..."
    override val composerSpintaxValidated = "Spintax Doğrulandı"
    override val composerCharCount = { chars: Int, segments: Int -> "$chars karakter • $segments mesaj parçası" }
    override val composerAiRewriteBtn = "Yapay Zekâ ile Yeniden Yaz ve Çeşitlendir"
    override val composerToneFriendly = "Samimi"
    override val composerToneShorter = "Kısa"
    override val composerToneMaxVariance = "En Çok Çeşitlilik"
    override val composerLivePreviewTitle = "CANLI ÖNİZLEME"
    override val composerOnlinePreview = "Çevrimiçi Önizleme"
    override val composerDynamicJitterTitle = "Rastgele Bekleme Süresi"
    override val composerSafeDelayRange = "Güvenli Bekleme: 25-60 sn"
    override val composerJitterFast = "Hızlı (Agresif)"
    override val composerJitterMetaCompliant = "Standart • Rastgele Süre"
    override val composerJitterRelaxed = "Yavaş"
    override val composerLaunchBtn = "Toplu Gönderimi Başlat"
    override val composerLaunchContactsCount = { count: Int -> "$count Kişi" }
    override val composerCompanyVariableTitle = "{Company} Değeri (İşletme / Marka Adı):"
    override val composerCompanyPlaceholder = "Örnek: Kirimrasa / Butik Mağaza"
    override val composerCustom1VariableTitle = "{Custom_1} Değeri (Promosyon Kodu / Bağlantı / Diğer Bilgi):"
    override val composerCustom1Placeholder = "Örnek: INDIRIM30 / https://wa.me/..."
    override val composerCustomVariablesDesc = "{Nickname}, Kişiler sekmesinde her kişiye verdiğiniz hitap adından otomatik alınır."
    override val composerSetVariablesBtn = "{Company} & {Custom_1} Değerlerini Ayarla"
    override val composerBubbleNumbered = { num: Int -> "Mesaj Balonu #$num" }
    override val composerRemoveBubble = "Balonu Sil"
    override val composerAddBubbleBtn = "+ Mesaj Balonu Ekle (en fazla 3)"
    override val composerAiGenerating = "Yapay zekâ ile mesaj çeşitleri hazırlanıyor..."
    override val composerMediaPickerError = { err: String -> "Dosya seçici açılamadı: $err" }
    override val composerDualSyncLabel = "İki Hesap"

    override val contactsTitle = "Kişi Yönetimi"
    override val contactsSubtitle = "Rehber Senkronizasyonu & Kişiselleştirme"
    override val contactsSyncBtn = "Senkronize Et"
    override val contactsSyncedAgo = "2 dk önce"
    override val contactsTotalMetric = "Toplam"
    override val contactsSelectedMetric = "Seçilen"
    override val contactsNicknamesMetric = "Hitap Adı"
    override val contactsSearchPlaceholder = "İsim, numara veya etiket ara..."
    override val contactsTagAll = { count: Int -> "Tümü ($count)" }
    override val contactsTagSelected = { count: Int -> "Seçilenler ($count)" }
    override val contactsTagFamily = { count: Int -> "Aile ($count)" }
    override val contactsTagFriends = { count: Int -> "Arkadaşlar ($count)" }
    override val contactsTagBusiness = { count: Int -> "İş ($count)" }
    override val contactsNewTagBtn = "Yeni Etiket"
    override val contactsSelectAllInTag = { tag: String, count: Int -> "$tag etiketli tüm kişileri seç ($count)" }
    override val contactsBulkAssignTagBtn = "Toplu Etiket Ata"
    override val contactsCallNameLabel = "Hitap Adı (Samimi Ad)"
    override val contactsGreetingPreview = { name: String -> "Önizleme: \"Merhaba $name! 👋\"" }
    override val contactsQuickFormat = "Hızlı format:"
    override val contactsFormatFirstName = "Sadece ilk ad"
    override val contactsFormatRemoveNotes = "Parantez içindeki notları sil"
    override val contactsFormatAddHello = "+ Merhaba / Selam"
    override val contactsImportCsvBtn = "CSV İçe Aktar"
    override val contactsImportDialogTitle = "Kişileri CSV'den İçe Aktar"
    override val contactsImportPickFile = "Cihazdan CSV Dosyası Seç"
    override val contactsImportLoadSample = "Örnek Veri Yükle (5 Kişi)"
    override val contactsImportResult = { imported: Int, total: Int -> "$total satırdan $imported kişi başarıyla içe aktarıldı" }
    override val contactsSyncing = "Senkronize ediliyor..."
    override val contactsSelectAllInView = "Tümünü Seç"
    override val contactsDeselectAllInView = "Seçimi Kaldır"
    override val contactsAllSelectedInViewCount = { count: Int -> "Tümü Seçildi ($count Kişi)" }
    override val contactsSelectAllInViewCount = { count: Int -> "Tüm Kişileri Seç ($count)" }
    override val contactsClearSearch = "Aramayı temizle"
    override val contactsAutoKak = "Otomatik: 'efendim'"
    override val contactsManageTagsTitle = "Kişi Etiketlerini Yönet"
    override val contactsManageTagsDesc = "Bu kişiye eklenecek etiket kategorilerini seçin:"
    override val contactsAddNewTagTitle = "Yeni Etiket Kategorisi Ekle"
    override val contactsAddNewTagDesc = "Kişileri gruplandırmak için yeni bir etiket oluşturun (ör: VIP, Müşteri, Bayi, Tedarikçi)."
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

    override val queueTitle = "Canlı Gönderim Sırası"
    override val queueCampaignName = "Kampanya #104: Bahar İndirimi"
    override val queueMsgPerMin = "1,2 mesaj/dk"
    override val queueDualNodeActive = "İki WA Hesabı • Senkronizasyon Açık"
    override val queueSafeBadge = "Güvenli • Seviye 3"
    override val queueSentCount = { sent: Int, total: Int -> "$sent / $total Gönderildi" }
    override val queueCompletePercentage = { pct: Int -> "%$pct Tamamlandı" }
    override val queueRecipientsRemaining = { count: Int -> "$count Kalan Alıcı" }
    override val queueEstTimeLeft = "Tahmini kalan süre: 14 dk 10 sn"
    override val queueWaitingJitter = { sec: Int -> "Güvenlik beklemesi: ${sec} sn" }
    override val queueTypingCadenceActive = "Doğal yazma hızı açık"
    override val queueMetricSuccess = "Başarılı"
    override val queueMetricAvgDelay = "Ort. Bekleme"
    override val queueMetricFlagAlert = "Uyarı"
    override val queuePauseBtn = "Gönderimi Duraklat"
    override val queueStopBtn = "Gönderimi Durdur"
    override val queueExportCsvBtn = "CSV Raporunu Dışa Aktar"
    override val queueExportSuccessToast = { count: Int -> "CSV raporu başarıyla dışa aktarıldı ($count kayıt)" }
    override val queueExportFailed = { err: String -> "Rapor dışa aktarılamadı: $err" }
    override val queueFilterAll = "Tümü"
    override val queueFilterActive = "Aktif"
    override val queueFilterDelivered = "Gönderildi"
    override val queueFilterPending = "Bekliyor"

    override val settingsTitle = "Hesaplar ve Güvenlik Ayarları"
    override val settingsSubtitle = "Ban koruması, çoklu hesapla gönderim ve doğal gönderim hızı"
    override val settingsShieldActive = "Koruma Seviye 3 Açık"
    override val settingsGuardrailVersion = "Koruma v4.2"
    override val settingsEnginesTitle = "Bağlı Hesaplar"
    override val settingsEnginesCount = "2 Hesap Ayarlandı"
    override val settingsNode1Title = "WhatsApp Kişisel"
    override val settingsNode1NodeNum = "Hesap 1"
    override val settingsConnected = "Bağlı"
    override val settingsUptime = "Çalışma Süresi:"
    override val settingsHealth = "Durum:"
    override val settingsTestPingBtn = "Ping Testi"
    override val settingsPinging = "Ping Gönderiliyor (18ms)..."
    override val settingsPongResult = "Pong! 18ms"
    override val settingsDisconnectBtn = "Bağlantıyı Kes"
    override val settingsNode2Title = "WhatsApp Business"
    override val settingsNode2NodeNum = "Hesap 2"
    override val settingsPairingReady = "Eşlemeye Hazır"
    override val settingsPairingDescription = "İkinci bir telefona veya QR kodu taramaya gerek kalmadan bu cihazda hemen bağlayın."
    override val settingsExpiresIn = "Kalan süre"
    override val settingsCopyOpenBtn = "Kodu Kopyala ve WhatsApp'ı Aç"
    override val settingsCodeCopied = "Kod Panoya Kopyalandı!"
    override val settingsSwitchToQr = "Veya QR kodu ile bağlanın"
    override val settingsAntiBanTitle = "Ban Koruması (Doğal Gönderim)"
    override val settingsRecommended = "Önerilen"
    override val settingsJitterTitle = "Rastgele Bekleme Aralığı"
    override val settingsJitterSubtitle = "Mesajlar arasında doğal, rastgele bekleme"
    override val settingsPresetConservative = "Temkinli (35-75 sn)"
    override val settingsPresetOptimal = "Dengeli (25-65 sn)"
    override val settingsPresetTurbo = "Turbo (15-30 sn)"
    override val settingsTypingSimTitle = "\"Yazıyor...\" Göster"
    override val settingsTypingSimSubtitle = "Göndermeden önce karşı tarafta \"yazıyor...\" görünür"
    override val settingsAutoRotateTitle = "Hesaplar Arası Otomatik Geçiş"
    override val settingsAutoRotateSubtitle = "Mesajları bağlı hesaplar arasında eşit dağıtır (%50/%50)"
    override val settingsAutoRotatePattern = "Etkin düzen: sırayla Hesap 1 ➔ Hesap 2"
    override val settingsCooldownTitle = "Belirli Aralıklarla Mola"
    override val settingsCooldownSubtitle = "Otomatik spam tespitine takılmamak için düzenli aralıklarla ara verir"
    override val settingsPauseDuration = "Mola Süresi"
    override val settingsBatchSize = "Mola Öncesi Mesaj Sayısı"
    override val settingsCooldownNotice = { count: Int -> "Her $count mesajda bir otomatik mola verilir" }
    override val settingsAiToneTitle = "Varsayılan Yapay Zekâ Yazım Tonu"
    override val settingsSpintaxEngineTag = "Spintax Motoru"
    override val settingsToneCasual = "Gündelik"
    override val settingsToneFriendlyPicker = "Samimi"
    override val settingsToneFormal = "Resmi"
    override val settingsAiToneDesc = "Mesaj ekranında tek dokunuşla, WhatsApp spam filtresine takılmayan farklı cümleler üretir."
    override val settingsBaileysTitle = "Baileys Ağ Geçidi"
    override val settingsBaileysSubtitle = "Tekrar tarama gerektirmeyen çoklu cihaz ağ geçidi"
    override val settingsBaileysToggle = "Baileys Ağ Geçidini Kullan"
    override val settingsBaileysUrlLabel = "AĞ GEÇİDİ URL'Sİ"
    override val settingsBaileysKeyLabel = "API ANAHTARI (İSTEĞE BAĞLI)"
    override val settingsBaileysTestBtn = "Ağ Geçidi Bağlantısını Test Et"
    override val settingsLanguageTitle = "Uygulama Dili (Language)"
    override val settingsLanguageSubtitle = "Arayüz dilini anında değiştirin"
    override val settingsSelectIndonesian = "Bahasa Indonesia"
    override val settingsSelectTurkish = "Türkçe"
    override val settingsWaPersonalDetected = "Telefonda yüklü (WhatsApp)"
    override val settingsWaPersonalNotDetected = "WhatsApp Uygulaması Yüklü Değil"
    override val settingsWaBusinessDetected = "Telefonda yüklü (WhatsApp Business)"
    override val settingsWaBusinessNotDetected = "WhatsApp Business Yüklü Değil"
    override val settingsReadyToUse = "Kullanıma Hazır"
    override val settingsNotAvailable = "Henüz Yok"
    override val settingsYourWaNumber = "WhatsApp Numaranız:"
    override val settingsYourWbNumber = "WhatsApp Business Numaranız:"
    override val settingsInputPhonePlaceholder = "WhatsApp numaranızı yazın (ör: +90 555 123 4567)"
    override val settingsOpenWaBtn = "WhatsApp'ı Aç"
    override val settingsOpenWbBtn = "WA Business'ı Aç"
    override val settingsCheckingStatus = "Kontrol ediliyor..."
    override val settingsReadyStatus = "Kullanıma Hazır!"
    override val settingsFailedStatus = "Başarısız"
    override val settingsTestReadinessBtn = "Kontrol Et"
    override val settingsStatusActive = "AÇIK"
    override val settingsStatusInactive = "KAPALI"
    override val settingsBaileysExplanation = "Çoklu cihaz protokolüyle yüksek hızlı toplu mesaj gönderimi için yerel veya uzak Baileys Node.js ağ geçidini kullanın."
    override val settingsThemeTitle = "Tema ve Görünüm"
    override val settingsThemeSubtitle = "Açık veya koyu cam görünümlü temayı seçin"
    override val settingsThemeDark = "Koyu Mod (Apple Glass)"
    override val settingsThemeLight = "Açık Mod (Apple Glass)"
    override val settingsThemeSystem = "Sistem Varsayılanı"
    override val settingsNode1PhoneLabel = "WhatsApp Kişisel"
    override val settingsNode2PhoneLabel = "WhatsApp Business"
    override val settingsPairingCodeBtn = "8 Haneli Eşleme Kodu İste"

    override val tutorialTitle = "Kirimrasa Kullanım Rehberi"
    override val tutorialStep1Title = "Kirimrasa'ya Hoş Geldiniz"
    override val tutorialStep1Desc = "Modern cam görünümlü tasarımıyla, mesajlarınızın bot gibi değil sıcak ve samimi görünmesini sağlayan kişisel WhatsApp toplu mesaj uygulaması."
    override val tutorialStep2Title = "Hızlı Çoklu Cihaz Gönderimi"
    override val tutorialStep2Desc = "Sürekli karekod taraması gerekmeden arka planda toplu gönderim için yerleşik 'Çoklu Cihaz Ağ Geçidi'ni kullanın; bağlantınız her zaman kararlı ve güvende kalsın."
    override val tutorialStep3Title = "Kişisel Değişkenler & Anti-Spam"
    override val tutorialStep3Desc = "Kişilere hitap adlarıyla seslenmek için {Nickname} değişkenini kullanın, cümle çeşitliliği için Spintax'ı açın ve numaranızı korumak için güvenli bekleme sürelerini açık tutun."
    override val tutorialStep4Title = "Akıllı Kişi Yönetimi"
    override val tutorialStep4Desc = "Kişileri tek dokunuşla seçin, 'Seçilenler' sekmesinde görüntüleyin ve tek bir düğmeyle aynı anda etiketleyin."
    override val tutorialNextBtn = "İleri"
    override val tutorialStartBtn = "Kullanmaya Başla"
    override val tutorialSkipBtn = "Geç"
    override val settingsReopenTutorialBtn = "Kullanım Rehberini Aç"
    override val settingsUpdateTitle = "Uygulama Güncellemesi"
    override val settingsUpdateDesc = "Kirimrasa'nın en son sürümünü kontrol edin ve anında yükleyin"
    override val settingsCheckUpdateBtn = "Güncellemeleri Kontrol Et"
    override val settingsCheckingUpdates = "Kontrol ediliyor..."
    override val settingsAppUpToDate = "Uygulamanız zaten güncel ✓"

    // Extracted from hardcoded UI text
    override val updateApkLabel: String = "Resmi APK"
    override val updateAvailableTitle: String = "Güncelleme Var!"
    override val updateReleaseNotes: String = "Yenilikler:"
    override val updateDefaultChangelog: String = "Hız ve kararlılık iyileştirmeleri."
    override val updateNowBtn: String = "Şimdi Güncelle"
    override val updateLaterBtn: String = "Sonra"
    override val updateDownloading: String = "Güncelleme indiriliyor..."
    override val updateDownloadHint: String = "İndirme bitince kurulum ekranı kendiliğinden açılır."
    override val updateReadyTitle: String = "Kuruluma Hazır!"
    override val updateReadyDesc: String = "Yeni sürümü kurmak için aşağıdaki düğmeye dokunun."
    override val updateInstallBtn: String = "Güncellemeyi Kur"
    override val updateDownloadFailedTitle: String = "İndirilemedi"
    override val retryBtn: String = "Tekrar Dene"
    override val updateUpToDateTitle: String = "Uygulamanız Güncel!"
    override val updateUpToDateDesc: (String) -> String = { v -> "Kirimrasa v$v en son sürüm." }
    override val updateCheckingTitle: String = "Güncelleme kontrol ediliyor..."
    override val updateCheckFailed: String = "Güncelleme kontrol edilemedi"
    override val updateDownloadError: (String) -> String = { m -> "İndirilemedi: $m" }
    override val updateApkMissing: String = "APK dosyası bulunamadı veya bozuk."
    override val updateInstallError: (String) -> String = { m -> "Kurulum yapılamadı: $m" }
    override val notifSendingChannel: String = "Toplu WhatsApp Gönderimi"
    override val notifSendingChannelDesc: String = "Toplu mesaj gönderimi sürerken durumu gösterir"
    override val notifUpdateChannel: String = "Kirimrasa Güncellemeleri"
    override val notifUpdateChannelDesc: String = "Kirimrasa'nın yeni sürümü çıkınca haber verir"
    override val notifUpdateTitle: (String) -> String = { v -> "Kirimrasa $v sürümü çıktı!" }
    override val notifUpdateBody: (String, String) -> String = { v, s -> "Yeni sürüm ($v) yayınlandı:\n$s\n\nİndirip kurmak için dokunun." }
    override val notifServiceRunning: String = "Gönderim hizmeti çalışıyor..."
    override val notifServiceTitle: String = "Kirimrasa - Çalışıyor"
    override val notifServiceContent: String = "Mesajlar güvenli aralıklarla gönderiliyor..."
    override val notifDoneTitle: String = "Gönderim Tamamlandı!"
    override val notifDoneBody: (Int) -> String = { n -> "$n mesajın tamamı gönderildi." }
    override val notifTypingTitle: String = "Mesaj yazılıyor..."
    override val notifTypingBody: (String, Int, Int) -> String = { name, i, n -> "$name için mesaj yazılıyor ($i/$n)" }
    override val notifSendingTitle: (Int) -> String = { p -> "Mesajlar gönderiliyor (%$p)" }
    override val notifSendingBody: (Int, Int) -> String = { s, n -> "$s/$n gönderildi - sıradaki mesaja geçiliyor" }
    override val queueReadyTitle: String = "Gönderime hazır"
    override val gatewayConnecting: String = "WhatsApp'a bağlanılıyor..."
    override val gatewayNotLinked: String = "WhatsApp bağlı değil (Ayarlar'dan eşleme kodu alın)"
    override val gatewayAlreadyLinked: String = "WhatsApp bu cihazda zaten bağlı"
    override val waOpenFailed: (String) -> String = { m -> "WhatsApp açılamadı: $m" }
    override val waInstalled: String = "WhatsApp telefonda yüklü"
    override val waNotInstalled: String = "WhatsApp telefonda yüklü değil"
    override val csvReadFailed: (String) -> String = { m -> "CSV dosyası okunamadı: $m" }
    override val csvEmpty: String = "CSV dosyası boş"
    override val csvNoRecords: String = "CSV'de geçerli kişi bulunamadı"
    override val csvMissingPhone: String = "CSV başlığında telefon numarası sütunu yok"
    override val gatewayConnectFailed: String = "Ağ geçidine bağlanılamadı"
    override val codeCopied: (String) -> String = { c -> "$c kodu kopyalandı!" }
    override val waNotFound: String = "Bu cihazda WhatsApp bulunamadı"
    override val composerSampleMessage: String = "Merhaba {Nickname}! 👋\n{Company} olarak bu haftaki özel teklifimizi paylaşmak istedik."
    override val aiConnectFailed: String = "Yapay zeka hizmetine bağlanılamadı"
    override val filePickerFailed: (String) -> String = { m -> "Dosya seçici açılamadı: $m" }
    override val allContacts: String = "Tüm Kişiler"
    override val composerGuideTitle: String = "Mesaj ve Değişken Rehberi"
    override val composerGuide1: String = "1. Spintax {A|B|C}: Her kişiye kelimelerden biri rastgele gider. Böylece mesajlar birbirinin aynısı olmaz ve spam gibi görünmez."
    override val composerGuide2: String = "2. {Nickname}: Kişiler sekmesinde verdiğiniz hitap adı otomatik yazılır."
    override val composerGuide3: String = "3. {Company} ve {Custom_1}: Firma adı ve kampanya bilgisi gibi değerler. '{Company} & {Custom_1} Değerlerini Ayarla' düğmesinden değiştirebilirsiniz."
    override val composerGuide4: String = "4. Çoklu balon: Uzun mesajı 2-3 balona böler. Balonlar arasında 3 saniye \"yazıyor...\" gösterilir, gerçek biri yazıyormuş gibi olur."
    override val syncResult: (Int, Int) -> String = { a, b -> "+$a yeni, $b güncellendi" }
    override val contactsPermissionNeeded: String = "Rehber izni gerekli"
    override val failedWith: (String) -> String = { m -> "Hata: $m" }
    override val csvImported: (Int) -> String = { n -> "CSV'den +$n kişi içe aktarıldı" }
    override val csvOpenFailed: String = "CSV dosyası açılamadı"
    override val csvImportTitle: String = "CSV İçe Aktar"
    override val queueStatePaused: String = "DURAKLATILDI"
    override val queueStateSending: String = "GÖNDERİLİYOR"
    override val queueStateIdle: String = "HAZIR"
    override val queueDone: String = "Bitti"
    override val queueTypingTo: (String) -> String = { n -> "Mesaj yazılıyor: $n" }
    override val queuePausedBanner: String = "Gönderim duraklatıldı"
    override val queueWaitingSafeDelay: (Int) -> String = { s -> "Güvenli bekleme (${s} sn)" }
    override val queueReadyToSend: String = "Göndermeye hazır"
    override val queueEmpty: String = "Henüz gönderim yok. 'Mesaj Oluştur' sekmesinde mesajınızı yazın ve gönderimi başlatın."
    override val queueFilterEmpty: String = "Bu filtrede kişi yok"
    override val queueTypingBadge: String = "YAZIYOR (2 sn)"
    override val campaignDefaultTitle: String = "Toplu WhatsApp Gönderimi"
    override val sendFailed: String = "Gönderilemedi"
    override val reportSubject: (String) -> String = { t -> "Gönderim Raporu: $t" }
    override val reportExportChooser: String = "CSV Raporunu Paylaş"
    override val reportExported: (Int) -> String = { n -> "CSV raporu dışa aktarıldı ($n kayıt)" }
    override val reportExportFailed: (String) -> String = { m -> "Rapor dışa aktarılamadı: $m" }
    override val tutorialMenuDesc: String = "Kullanım Rehberi"
    override val toggleThemeDesc: String = "Temayı Değiştir"
    override val fallbackBroadcastMessage: String = "Merhaba {Nickname}, {Company} bu haftaya özel bir teklif sunuyor! {Custom_1} kodunu kullanın."
    override val waPersonalDetected: String = "Telefonda yüklü (WhatsApp)"
    override val waPersonalMissing: String = "WhatsApp yüklü değil"
    override val readyLabel: String = "Hazır"
    override val notAvailableLabel: String = "Henüz Yok"
    override val yourWaNumber: String = "WhatsApp numaranız:"
    override val waNumberHint: String = "WhatsApp numaranızı yazın (örn: +90 532 123 45 67)"
    override val waPersonalNotInstalledToast: String = "Telefonda WhatsApp yüklü değil"
    override val openWhatsAppBtn: String = "WhatsApp'ı Aç"
    override val readyToUse: String = "Kullanıma hazır!"
    override val failedLabel: String = "Başarısız"
    override val testReadinessBtn: String = "Kontrol Et"
    override val waBusinessDetected: String = "Telefonda yüklü (WhatsApp Business)"
    override val waBusinessMissing: String = "WhatsApp Business yüklü değil"
    override val yourWbNumber: String = "WhatsApp Business numaranız:"
    override val wbNumberHint: String = "WhatsApp Business numaranızı yazın (örn: +90 532 123 45 67)"
    override val waBusinessNotInstalledToast: String = "Telefonda WhatsApp Business yüklü değil"
    override val openWaBusinessBtn: String = "WhatsApp Business'ı Aç"
    override val activeLabel: String = "AÇIK"
    override val inactiveLabel: String = "KAPALI"
    override val gatewayDesc: String = "Toplu mesajlar, bağladığınız WhatsApp hesabı üzerinden ağ geçidi aracılığıyla gönderilir."
    override val apiKeyHint: String = "Ağ geçidi API anahtarı (yoksa boş bırakın)"
    override val gatewayConnectedLatency: (Long) -> String = { ms -> "Bağlı (${ms} ms)" }
    override val minutesShort: (Int) -> String = { n -> "$n dk" }
    override val messagesShort: (Int) -> String = { n -> "$n mesaj" }
    override val randomDelayLabel: String = "Rastgele Bekleme"
    override val qrLinkTitle: String = "QR Kodu ile Bağla"
    override val qrLinkDesc1: String = "8 haneli eşleme kodunu öneriyoruz: aynı telefonda yapılır, başka bir telefonun kamerası gerekmez."
    override val qrLinkDesc2: String = "Eşleme kodu için: 'Kodu Kopyala ve WhatsApp'ı Aç'a dokunun, sonra WhatsApp'ta Ayarlar > Bağlı cihazlar > Cihaz bağla > Bunun yerine telefon numarasıyla bağlan yolunu izleyin."
    override val connectedViaGateway: (String) -> String = { n -> "Ağ geçidi üzerinden bağlı ($n)" }
    override val requestPairingCodeBtn: String = "8 Haneli Eşleme Kodu Al"
    override val yourPairingCode: String = "WhatsApp eşleme kodunuz:"
    override val copyCodeOpenWaBtn: String = "Kodu Kopyala ve WhatsApp'ı Aç"
    override val nicknameFallback: String = "efendim"
    override val greetingWord: String = "Merhaba"
    override val understoodBtn: String = "Anladım"
    override val roundRobinLabel: String = "Sırayla"
    override val queueBadgeWaiting: String = "BEKLİYOR"
    override val queueBadgeFailed: String = "BAŞARISIZ"
    override val queueBadgeSent: String = "GÖNDERİLDİ"
}

val LocalAppStrings = compositionLocalOf<AppStrings> { IndonesianStrings }
