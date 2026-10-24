package com.wabulk.sender.data.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.wabulk.sender.domain.model.MediaType

/** Copy for the Send (composer) tab. */
interface ComposerTexts {
    val title: String
    val help: String
    val personal: String
    val business: String
    val dual: String
    val connected: (String) -> String
    val connecting: String
    val notConnected: String
    val to: String
    val selectedContacts: (Int) -> String
    val allContacts: String
    val allContactsCount: (Int) -> String
    val noContacts: String
    val writeMessage: String
    val bubble: (Int) -> String
    val removeBubble: (Int) -> String
    val placeholder: String
    val addNickname: String
    val addCompany: String
    val addPromo: String
    val addBubble: String
    val sendFileIn: String
    val separate: String
    val sentWithBubble: String
    val sentSeparately: String
    val removeAttachment: String
    val mediaLabel: (MediaType) -> String
    val attach: String
    val attachFull: String
    val aiTidy: String
    val aiWorking: String
    val aiNeedText: String
    val aiStyle: String
    val toneFriendly: String
    val toneShorter: String
    val toneVaried: String
    val toneHeartfelt: String
    val toneCasual: String
    val tonePolite: String
    val preview: String
    val typing: String
    val previewEmpty: String
    val sampleName: String
    val moreOptions: String
    val moreOptionsHint: String
    val companyLabel: String
    val custom1Label: String
    val nicknameNote: String
    val variationTitle: String
    val variationHelp: String
    val delayTitle: String
    val delayInfo: (min: Int, max: Int, batch: Int, pauseMin: Int) -> String
    val companyName: String
    val promoName: String
    val emptyVarsWarning: (String) -> String
    val sendTo: (Int) -> String
    val sendAll: String
    val needContacts: String
    val needMessage: String
    val safeDelayOn: String
    val estimate: (String) -> String
    val duration: (Int) -> String
}

object IndonesianComposerTexts : ComposerTexts {
    override val title = "Kirim Pesan"
    override val help = "Bantuan"
    override val personal = "Pribadi"
    override val business = "Bisnis"
    override val dual = "Dua Akun"
    override val connected = { phone: String -> if (phone.isBlank()) "Tersambung" else "Tersambung · $phone" }
    override val connecting = "Sedang menyambungkan…"
    override val notConnected = "Belum tersambung"
    override val to = "Kepada"
    override val selectedContacts = { n: Int -> "$n kontak dipilih" }
    override val allContacts = "Semua kontak"
    override val allContactsCount = { n: Int -> "Semua kontak · $n" }
    override val noContacts = "Belum ada kontak, ketuk untuk menambah"
    override val writeMessage = "Tulis pesan"
    override val bubble = { n: Int -> "Balon $n" }
    override val removeBubble = { n: Int -> "Hapus balon $n" }
    override val placeholder = "Tulis pesanmu di sini…"
    override val addNickname = "+ Nama panggilan"
    override val addCompany = "+ Nama toko"
    override val addPromo = "+ Kode promo"
    override val addBubble = "+ Balon baru"
    override val sendFileIn = "Kirim file di:"
    override val separate = "Terpisah"
    override val sentWithBubble = "dikirim bersama balon ini"
    override val sentSeparately = "dikirim sendiri setelah pesan"
    override val removeAttachment = "Hapus lampiran"
    override val mediaLabel = { type: MediaType ->
        when (type) {
            MediaType.IMAGE -> "Foto"
            MediaType.VIDEO -> "Video"
            MediaType.AUDIO -> "Suara"
            MediaType.DOCUMENT -> "Dokumen"
        }
    }
    override val attach = "Lampirkan"
    override val attachFull = "Maksimal 5 lampiran"
    override val aiTidy = "Rapikan (AI)"
    override val aiWorking = "Merapikan…"
    override val aiNeedText = "Tulis pesan dulu, lalu rapikan."
    override val aiStyle = "Gaya tulisan AI"
    override val toneFriendly = "Ramah"
    override val toneShorter = "Singkat"
    override val toneVaried = "Banyak variasi"
    override val toneHeartfelt = "Menyentuh"
    override val toneCasual = "Santai"
    override val tonePolite = "Sopan"
    override val preview = "Pratinjau"
    override val typing = "sedang mengetik…"
    override val previewEmpty = "Pesanmu akan tampil di sini"
    override val sampleName = "Budi"
    override val moreOptions = "Opsi lainnya"
    override val moreOptionsHint = "Nama toko, jeda"
    override val companyLabel = "Nama toko (mengisi {Company})"
    override val custom1Label = "Kode promo (mengisi {Custom_1})"
    override val nicknameNote = "{Nickname} otomatis diganti nama panggilan tiap kontak. Atur di tab Kontak."
    override val variationTitle = "Variasi kata"
    override val variationHelp = "Tulis {Halo|Hai|Selamat pagi} dan tiap kontak akan menerima salah satu kata secara acak, jadi pesannya tidak sama persis."
    override val delayTitle = "Jeda aman"
    override val delayInfo = { min: Int, max: Int, batch: Int, pauseMin: Int ->
        "Menunggu $min–$max detik antar kontak dan 3–6 detik antar balon, lalu istirahat $pauseMin menit setiap $batch pesan. Ubah di Pengaturan."
    }
    override val companyName = "Nama toko"
    override val promoName = "Kode promo"
    override val emptyVarsWarning = { names: String -> "$names masih kosong dan akan terkirim kosong. Ketuk untuk mengisi." }
    override val sendTo = { n: Int -> "Kirim ke $n kontak" }
    override val sendAll = "Kirim ke semua kontak"
    override val needContacts = "Tambahkan kontak dulu di tab Kontak"
    override val needMessage = "Tulis pesan atau lampirkan file dulu"
    override val safeDelayOn = "Jeda aman aktif"
    override val estimate = { d: String -> "Selesai sekitar $d · jeda aman aktif" }
    override val duration = { m: Int ->
        when {
            m < 60 -> "$m menit"
            m % 60 == 0 -> "${m / 60} jam"
            else -> "${m / 60} jam ${m % 60} menit"
        }
    }
}

object TurkishComposerTexts : ComposerTexts {
    override val title = "Mesaj Gönder"
    override val help = "Yardım"
    override val personal = "Kişisel"
    override val business = "İşletme"
    override val dual = "İki Hesap"
    override val connected = { phone: String -> if (phone.isBlank()) "Bağlı" else "Bağlı · $phone" }
    override val connecting = "Bağlanıyor…"
    override val notConnected = "Bağlı değil"
    override val to = "Kime"
    override val selectedContacts = { n: Int -> "$n kişi seçildi" }
    override val allContacts = "Tüm kişiler"
    override val allContactsCount = { n: Int -> "Tüm kişiler · $n" }
    override val noContacts = "Henüz kişi yok, eklemek için dokunun"
    override val writeMessage = "Mesajı yazın"
    override val bubble = { n: Int -> "$n. balon" }
    override val removeBubble = { n: Int -> "$n. balonu sil" }
    override val placeholder = "Mesajınızı buraya yazın…"
    override val addNickname = "+ Hitap adı"
    override val addCompany = "+ Mağaza adı"
    override val addPromo = "+ İndirim kodu"
    override val addBubble = "+ Yeni balon"
    override val sendFileIn = "Dosyayı gönder:"
    override val separate = "Ayrı"
    override val sentWithBubble = "bu balonla gönderilir"
    override val sentSeparately = "mesajdan sonra ayrı gönderilir"
    override val removeAttachment = "Eki kaldır"
    override val mediaLabel = { type: MediaType ->
        when (type) {
            MediaType.IMAGE -> "Fotoğraf"
            MediaType.VIDEO -> "Video"
            MediaType.AUDIO -> "Ses"
            MediaType.DOCUMENT -> "Belge"
        }
    }
    override val attach = "Dosya ekle"
    override val attachFull = "En fazla 5 dosya eklenebilir"
    override val aiTidy = "Yapay zekâ ile düzenle"
    override val aiWorking = "Düzenleniyor…"
    override val aiNeedText = "Önce mesajı yazın, sonra düzenleyin."
    override val aiStyle = "Yapay zekâ yazım tarzı"
    override val toneFriendly = "Samimi"
    override val toneShorter = "Kısa"
    override val toneVaried = "Çok çeşitli"
    override val toneHeartfelt = "İçten"
    override val toneCasual = "Rahat"
    override val tonePolite = "Kibar"
    override val preview = "Önizleme"
    override val typing = "yazıyor…"
    override val previewEmpty = "Mesajınız burada görünecek"
    override val sampleName = "Ayşe"
    override val moreOptions = "Diğer seçenekler"
    override val moreOptionsHint = "Mağaza adı, bekleme"
    override val companyLabel = "Mağaza adı ({Company} yerine yazılır)"
    override val custom1Label = "İndirim kodu ({Custom_1} yerine yazılır)"
    override val nicknameNote = "{Nickname} her kişinin hitap adıyla otomatik değişir. Kişiler sekmesinden ayarlayın."
    override val variationTitle = "Kelime çeşitliliği"
    override val variationHelp = "{Merhaba|Selam|İyi günler} yazarsanız her kişiye bu kelimelerden biri rastgele gider, böylece mesajlar birebir aynı olmaz."
    override val delayTitle = "Güvenli bekleme"
    override val delayInfo = { min: Int, max: Int, batch: Int, pauseMin: Int ->
        "Kişiler arasında $min–$max sn, balonlar arasında 3–6 sn beklenir; her $batch mesajda $pauseMin dk mola verilir. Ayarlardan değiştirebilirsiniz."
    }
    override val companyName = "Mağaza adı"
    override val promoName = "İndirim kodu"
    override val emptyVarsWarning = { names: String -> "$names boş, mesajda boş gidecek. Doldurmak için dokunun." }
    override val sendTo = { n: Int -> "$n kişiye gönder" }
    override val sendAll = "Tüm kişilere gönder"
    override val needContacts = "Önce Kişiler sekmesinden kişi ekleyin"
    override val needMessage = "Önce mesaj yazın veya dosya ekleyin"
    override val safeDelayOn = "Güvenli bekleme açık"
    override val estimate = { d: String -> "Yaklaşık $d sürer · güvenli bekleme açık" }
    override val duration = { m: Int ->
        when {
            m < 60 -> "$m dakika"
            m % 60 == 0 -> "${m / 60} saat"
            else -> "${m / 60} saat ${m % 60} dakika"
        }
    }
}

@Composable
@ReadOnlyComposable
fun composerTexts(): ComposerTexts = if (isTurkishUi()) TurkishComposerTexts else IndonesianComposerTexts
