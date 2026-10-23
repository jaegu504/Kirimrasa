package com.wabulk.sender.data.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/** Copy for the Contacts tab and its dialogs. */
interface ContactsTexts {
    val title: String
    val syncButton: String
    val syncing: String
    val permissionDenied: String
    val importCsv: String
    val searchPlaceholder: String
    val clearSearch: String
    val all: (Int) -> String
    val selectedFilter: (Int) -> String
    val newLabel: String
    val selectedCaption: (Int) -> String
    val selectAll: String
    val deselectAll: String
    val calledAs: (String) -> String
    val noNickname: String
    val selected: String
    val notSelected: String
    val editContact: String
    val nicknameLabel: String
    val nicknameHint: (String) -> String
    val firstNameOnly: String
    val removeNotes: String
    val addGreeting: String
    val labels: String
    val barCount: (Int) -> String
    val giveLabel: String
    val use: String
    val emptyNoContacts: String
    val emptyNoMatch: String
    val emptySelected: String
    val newLabelDesc: String
    val labelNamePlaceholder: String
    val batchTitle: (Int) -> String
    val batchDesc: String
    val typeNewLabel: String
    val manageDesc: String
    val importDesc: String
    val importPickFile: String
    val importSample: String
    val cancel: String
    val save: String
    val add: String
    val done: String
    val close: String
}

object IndonesianContactsTexts : ContactsTexts {
    override val title = "Kontak"
    override val syncButton = "Ambil dari HP"
    override val syncing = "Mengambil…"
    override val permissionDenied = "Izinkan akses kontak agar bisa mengambil kontak dari HP."
    override val importCsv = "Impor dari file CSV"
    override val searchPlaceholder = "Cari nama atau nomor"
    override val clearSearch = "Hapus pencarian"
    override val all = { n: Int -> "Semua $n" }
    override val selectedFilter = { n: Int -> "Dipilih $n" }
    override val newLabel = "Label baru"
    override val selectedCaption = { n: Int -> "$n dipilih" }
    override val selectAll = "Pilih semua"
    override val deselectAll = "Batal pilih"
    override val calledAs = { nick: String -> "Dipanggil \"$nick\"" }
    override val noNickname = "Belum ada nama panggilan"
    override val selected = "Dipilih"
    override val notSelected = "Tidak dipilih"
    override val editContact = "Ubah nama panggilan dan label"
    override val nicknameLabel = "Nama panggilan"
    override val nicknameHint = { fallback: String -> "Kosong = pakai \"$fallback\"" }
    override val firstNameOnly = "Nama depan saja"
    override val removeNotes = "Buang catatan (…)"
    override val addGreeting = "Tambah sapaan"
    override val labels = "Label"
    override val barCount = { n: Int -> "$n kontak dipilih" }
    override val giveLabel = "Beri label"
    override val use = "Pakai"
    override val emptyNoContacts = "Belum ada kontak. Ketuk \"Ambil dari HP\" di atas."
    override val emptyNoMatch = "Tidak ada kontak yang cocok."
    override val emptySelected = "Belum ada kontak yang dipilih."
    override val newLabelDesc = "Kelompokkan kontak, misalnya VIP atau Pelanggan."
    override val labelNamePlaceholder = "Nama label"
    override val batchTitle = { n: Int -> "Beri label ke $n kontak" }
    override val batchDesc = "Pilih label, atau ketik label baru."
    override val typeNewLabel = "Ketik label baru"
    override val manageDesc = "Ketuk label untuk memasang atau melepasnya."
    override val importDesc = "Kolom: Nama, No HP, Panggilan, Perusahaan, Custom_1, Tag"
    override val importPickFile = "Pilih file CSV"
    override val importSample = "Coba dengan 5 kontak contoh"
    override val cancel = "Batal"
    override val save = "Simpan"
    override val add = "Tambah"
    override val done = "Selesai"
    override val close = "Tutup"
}

object TurkishContactsTexts : ContactsTexts {
    override val title = "Kişiler"
    override val syncButton = "Rehberden al"
    override val syncing = "Alınıyor…"
    override val permissionDenied = "Kişileri rehberden almak için rehber iznini verin."
    override val importCsv = "CSV dosyasından al"
    override val searchPlaceholder = "İsim veya numara ara"
    override val clearSearch = "Aramayı temizle"
    override val all = { n: Int -> "Tümü $n" }
    override val selectedFilter = { n: Int -> "Seçili $n" }
    override val newLabel = "Yeni etiket"
    override val selectedCaption = { n: Int -> "$n seçili" }
    override val selectAll = "Tümünü seç"
    override val deselectAll = "Seçimi kaldır"
    override val calledAs = { nick: String -> "Hitap: \"$nick\"" }
    override val noNickname = "Hitap adı yok"
    override val selected = "Seçili"
    override val notSelected = "Seçili değil"
    override val editContact = "Hitap adını ve etiketleri düzenle"
    override val nicknameLabel = "Hitap adı"
    override val nicknameHint = { fallback: String -> "Boşsa \"$fallback\" kullanılır" }
    override val firstNameOnly = "Sadece ilk ad"
    override val removeNotes = "Parantez notunu sil"
    override val addGreeting = "Selam ekle"
    override val labels = "Etiketler"
    override val barCount = { n: Int -> "$n kişi seçili" }
    override val giveLabel = "Etiket ver"
    override val use = "Kullan"
    override val emptyNoContacts = "Henüz kişi yok. Yukarıdan \"Rehberden al\"a dokunun."
    override val emptyNoMatch = "Eşleşen kişi yok."
    override val emptySelected = "Henüz seçili kişi yok."
    override val newLabelDesc = "Kişileri gruplayın, örneğin VIP veya Müşteri."
    override val labelNamePlaceholder = "Etiket adı"
    override val batchTitle = { n: Int -> "$n kişiye etiket ver" }
    override val batchDesc = "Bir etiket seçin veya yenisini yazın."
    override val typeNewLabel = "Yeni etiket yazın"
    override val manageDesc = "Eklemek veya kaldırmak için etikete dokunun."
    override val importDesc = "Sütunlar: İsim, Telefon, Takma ad, Şirket, Özel 1, Etiket"
    override val importPickFile = "CSV dosyası seç"
    override val importSample = "5 örnek kişiyle dene"
    override val cancel = "İptal"
    override val save = "Kaydet"
    override val add = "Ekle"
    override val done = "Tamam"
    override val close = "Kapat"
}

@Composable
@ReadOnlyComposable
fun contactsTexts(): ContactsTexts = if (isTurkishUi()) TurkishContactsTexts else IndonesianContactsTexts
