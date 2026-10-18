# Panduan Pasang Gateway WhatsApp di Hosting Gratis (Tanpa Butuh Laptop)

Gateway ini berfungsi sebagai jembatan protokol WhatsApp Multi-Device (Baileys) yang berjalan 24 jam di cloud internet. Setelah dipasang satu kali, **kamu dan teman-temanmu TIDAK PERLU menyalakan laptop lagi**, cukup gunakan aplikasi Android di ponsel masing-masing.

---

## Opsi 1: Pasang di Render.com (Paling Direkomendasikan — 100% Gratis & Tanpa Kartu Kredit)

Render menyediakan hosting gratis yang sangat stabil untuk server Node.js.

### Langkah-langkah (Bisa dilakukan dari browser HP atau komputer):

1. **Buka Situs Render:**
   * Kunjungi [https://render.com](https://render.com).
   * Daftar akun baru (bisa langsung klik *Continue with GitHub* atau pakai Email).
2. **Buat Web Service Baru:**
   * Di dashboard Render, klik tombol **"New +"** di kanan atas $\rightarrow$ pilih **"Web Service"**.
   * Pilih repositori GitHub proyek ini (jika sudah di-push ke GitHub Anda), atau klik **"Deploy from Git repository"**.
3. **Pengaturan Web Service:**
   * **Name:** `wa-bulk-gateway` (bebas)
   * **Region:** `Singapore` (terdekat dengan Indonesia/Asia, koneksi tercepat)
   * **Root Directory:** ketik `wa-gateway`
   * **Runtime:** `Node`
   * **Build Command:** `npm install`
   * **Start Command:** `npm start`
   * **Instance Type:** Pilih **Free** ($0 / month)
4. **Klik Deploy:**
   * Gulir ke paling bawah dan klik **"Create Web Service"**.
   * Tunggu sekitar 1–2 menit sampai statusnya menjadi **Live** (Hijau).
   * Di bagian atas, kamu akan mendapatkan link HTTPS resmi gratis, contohnya:
     `https://wa-bulk-gateway-xxxx.onrender.com`

---

## Opsi 2: Pasang di Koyeb (Alternatif Gratis Instan Tanpa Kartu Kredit)

1. Kunjungi [https://www.koyeb.com](https://www.koyeb.com).
2. Daftar akun gratis $\rightarrow$ klik **Create App**.
3. Pilih **GitHub** $\rightarrow$ pilih repo Anda.
4. Set Work Directory ke `wa-gateway` $\rightarrow$ pilih Free Instance $\rightarrow$ Deploy.
5. Anda akan mendapatkan URL HTTPS gratis Koyeb.

---

## Cara Menyambungkan ke Aplikasi WhatsApp Bulk di Ponsel:

1. Buka aplikasi **WhatsApp Bulk** di ponsel kamu.
2. Masuk ke tab **Pengaturan** (ikon gerigi).
3. Gulir ke bagian **Integrasi Baileys Gateway**:
   * Masukkan **URL Gateway**: tempel link yang kamu dapatkan tadi (contoh: `https://wa-bulk-gateway-xxxx.onrender.com`).
   * Aktifkan toggle **Gunakan Baileys Gateway**.
   * Ketuk tombol **Uji Sambungan Gateway** $\rightarrow$ Akan muncul notifikasi hijau *"Baileys Bridge Connected"*.
4. **Tautkan WhatsApp kamu:**
   * Di bagian **Node 1 (WhatsApp Personal)** atau **Node 2 (WhatsApp Business)**, ketik nomor teleponmu.
   * Ketuk tombol **Minta Kode Pairing**.
   * Dalam beberapa detik, 8 digit kode resmi dari server WhatsApp akan muncul di layar.
   * Buka WhatsApp di ponsel $\rightarrow$ ketuk titik tiga (⋮) $\rightarrow$ **Perangkat tertaut** $\rightarrow$ **Tautkan perangkat** $\rightarrow$ **Tautkan dengan nomor telepon saja** $\rightarrow$ ketikkan 8 kode tersebut.
5. **Selesai!** Status akan langsung berubah menjadi **TERHUBUNG** hijau.
   * Sekarang kamu bisa membuat pesan, memilih kontak, dan menjalankan kampanye pengiriman massal.
   * Pesan akan langsung terkirim di latar belakang tanpa membuka aplikasi WhatsApp di layar ponsel, tanpa meminjam layar, dan tanpa perlu menyalakan laptop!
