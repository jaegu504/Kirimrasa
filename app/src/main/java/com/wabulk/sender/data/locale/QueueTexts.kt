package com.wabulk.sender.data.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/** Copy for the Status (sending queue) tab. */
interface QueueTexts {
    val title: String
    val exportReport: String
    val sentWord: String
    val ringDescription: (Int, Int) -> String
    val phaseTyping: (String) -> String
    val phaseWaiting: (Int) -> String
    val phaseCooldown: (String) -> String
    val phasePaused: String
    val phaseDone: String
    val phaseReady: String
    val pause: String
    val resume: String
    val stop: String
    val stopConfirmTitle: String
    val stopConfirmBody: String
    val cancel: String
    val recipients: String
    val summary: (Int, Int, Int) -> String
    val filterFailed: String
    val rowTyping: String
    val rowSent: (String) -> String
    val rowSentNoTime: String
    val rowFailed: (String?) -> String
    val rowWaiting: String
    val emptyTitle: String
    val emptyHint: (String) -> String
}

object IndonesianQueueTexts : QueueTexts {
    override val title = "Status"
    override val exportReport = "Ekspor laporan CSV"
    override val sentWord = "terkirim"
    override val ringDescription = { sent: Int, total: Int -> "$sent dari $total terkirim" }
    override val phaseTyping = { name: String -> "Sedang mengetik untuk $name" }
    override val phaseWaiting = { sec: Int -> "Jeda aman · kontak berikutnya dalam $sec dtk" }
    override val phaseCooldown = { time: String -> "Istirahat otomatis · lanjut dalam $time" }
    override val phasePaused = "Dijeda · ketuk Lanjutkan untuk meneruskan"
    override val phaseDone = "Selesai"
    override val phaseReady = "Siap"
    override val pause = "Jeda"
    override val resume = "Lanjutkan"
    override val stop = "Hentikan"
    override val stopConfirmTitle = "Hentikan pengiriman?"
    override val stopConfirmBody = "Pesan yang belum terkirim tidak akan dikirim. Pesan yang sudah terkirim tetap aman."
    override val cancel = "Batal"
    override val recipients = "Penerima"
    override val summary = { ok: Int, failed: Int, waiting: Int -> "$ok sukses · $failed gagal · $waiting menunggu" }
    override val filterFailed = "Gagal"
    override val rowTyping = "Sedang mengetik…"
    override val rowSent = { time: String -> "Terkirim · $time" }
    override val rowSentNoTime = "Terkirim"
    override val rowFailed = { err: String? -> if (err.isNullOrBlank()) "Gagal terkirim" else "Gagal · $err" }
    override val rowWaiting = "Menunggu"
    override val emptyTitle = "Belum ada pengiriman"
    override val emptyHint = { tab: String -> "Tulis pesan di tab $tab, lalu kirim. Kemajuannya akan tampil di sini." }
}

object TurkishQueueTexts : QueueTexts {
    override val title = "Durum"
    override val exportReport = "CSV raporunu dışa aktar"
    override val sentWord = "gönderildi"
    override val ringDescription = { sent: Int, total: Int -> "$total mesajdan $sent tanesi gönderildi" }
    override val phaseTyping = { name: String -> "$name için yazılıyor" }
    override val phaseWaiting = { sec: Int -> "Güvenli bekleme · sonraki kişi $sec sn sonra" }
    override val phaseCooldown = { time: String -> "Otomatik mola · $time sonra devam" }
    override val phasePaused = "Duraklatıldı · devam etmek için Devam et'e dokunun"
    override val phaseDone = "Bitti"
    override val phaseReady = "Hazır"
    override val pause = "Duraklat"
    override val resume = "Devam et"
    override val stop = "Durdur"
    override val stopConfirmTitle = "Gönderim durdurulsun mu?"
    override val stopConfirmBody = "Henüz gönderilmemiş mesajlar gönderilmeyecek. Gönderilmiş mesajlar etkilenmez."
    override val cancel = "Vazgeç"
    override val recipients = "Alıcılar"
    override val summary = { ok: Int, failed: Int, waiting: Int -> "$ok başarılı · $failed başarısız · $waiting bekliyor" }
    override val filterFailed = "Başarısız"
    override val rowTyping = "Yazıyor…"
    override val rowSent = { time: String -> "Gönderildi · $time" }
    override val rowSentNoTime = "Gönderildi"
    override val rowFailed = { err: String? -> if (err.isNullOrBlank()) "Gönderilemedi" else "Başarısız · $err" }
    override val rowWaiting = "Bekliyor"
    override val emptyTitle = "Henüz gönderim yok"
    override val emptyHint = { tab: String -> "$tab sekmesinde mesajınızı yazıp gönderin. İlerleme burada görünür." }
}

@Composable
@ReadOnlyComposable
fun queueTexts(): QueueTexts = if (isTurkishUi()) TurkishQueueTexts else IndonesianQueueTexts
