{\rtf1\ansi\ansicpg1252\cocoartf2868
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;\f1\fswiss\fcharset0 Helvetica-Oblique;\f2\fnil\fcharset0 .AppleSystemUIFontMonospaced-Regular;
}
{\colortbl;\red255\green255\blue255;\red0\green0\blue0;}
{\*\expandedcolortbl;;\cssrgb\c0\c0\c0;}
\paperw11900\paperh16840\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\sl-560\sb340\sa120\partightenfactor0

\f0\fs40 \cf2 \up1 Tujuan dan Alasan Utama
\fs24 \cf0 \up0 \
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\sl-520\sa280\partightenfactor0

\fs34 \cf2 \up2 Aplikasi Android ini dirancang untuk membantu pengiriman pesan WhatsApp secara massal namun tetap terasa personal kepada jaringan kontak yang sudah mengenal pengguna, seperti keluarga, teman, maupun kenalan bisnis, tanpa biaya berlangganan API berbayar. Fokus utamanya adalah menghilangkan kesan kaku seperti pesan siaran robot melalui penggunaan nama panggilan akrab, pemecahan pesan menjadi beberapa gelembung obrolan beruntun, variasi kalimat berbasis AI yang menyesuaikan konteks hubungan, serta simulasi perilaku manusia di latar belakang agar akun tetap aman dari pemblokiran.
\fs24 \cf0 \up0 \
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\sl-560\sb340\sa120\partightenfactor0

\fs40 \cf2 \up1 Sasaran, Batasan, dan Hal yang Dihindari
\fs24 \cf0 \up0 \
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\sl-520\sa280\partightenfactor0

\fs34 \cf2 \up2 Sasaran utama proyek ini adalah menghadirkan aplikasi Android mandiri dengan antarmuka modern yang mampu menjalankan dua sesi akun sekaligus\'97WhatsApp biasa dan WhatsApp Business\'97di latar belakang tanpa mengganggu pemakaian ponsel sehari-hari. Aplikasi ini secara tegas tidak ditujukan untuk melakukan 
\f1\i spamming
\f0\i0  ke daftar nomor asing yang belum pernah berinteraksi sebelumnya, dan tidak akan mengubah atau menimpa nama asli yang tersimpan di dalam buku telepon bawaan sistem Android. Kegagalan terbesar yang wajib dihindari meskipun aplikasi berfungsi secara teknis adalah terjadinya pemblokiran nomor akibat pengiriman yang terlalu agresif, rusaknya variabel nama panggilan saat pesan diolah oleh AI, adanya tampilan antarmuka yang terasa patah-patah, atau terputusnya proses antrean di tengah jalan karena aplikasi dimatikan secara paksa oleh penghemat baterai sistem Android.
\fs24 \cf0 \up0 \
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\sl-560\sb340\sa120\partightenfactor0

\fs40 \cf2 \up1 Spesifikasi Fitur dan Alur Kerja yang Disepakati
\fs24 \cf0 \up0 \
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\sl-520\sa280\partightenfactor0

\fs34 \cf2 \up2 Antarmuka aplikasi mengacu pada hasil desain Google Stitch bertema hijau zamrud gelap dan abu-abu batu tulis dengan bilah navigasi empat halaman di bagian bawah serta dukungan dwibahasa penuh antara Bahasa Indonesia dan Bahasa Turki. Seluruh perpindahan halaman, penyaringan daftar kartu, pembukaan panel AI, hingga indikator hitung mundur dilengkapi animasi pegas dan efek denyut halus agar aplikasi terasa hidup. Halaman pertama adalah ruang peracik pesan (
\f1\i Composer
\f0\i0 ) yang memuat pengalih akun di bagian atas, karusel lampiran foto, gambar, dan video dari galeri ponsel, serta editor gelembung obrolan bertingkat (
\f1\i multi-bubble split message
\f0\i0 ). Pengguna dapat menambahkan gelembung pesan baru secara manual untuk memisahkan sapaan pembuka yang memuat variabel 
\f2 \{Nickname\}
\f0  dengan isi pesan inti, maupun memanfaatkan tombol asisten AI yang terhubung ke Cloudflare Worker untuk meneruskan perintah ke Groq dan Gemini. Di dalam panel AI tersebut terdapat sakelar opsional untuk menentukan apakah AI hanya perlu menghaluskan kalimat serta membuat variasi 
\f1\i spintax
\f0\i0  dalam satu balon pesan, atau sekaligus memotong draf yang panjang menjadi dua hingga tiga gelembung percakapan yang mengalir natural sesuai bahasa dan kategori penerima.
\fs24 \cf0 \up0 \

\fs34 \cf2 \up2 Halaman kedua adalah pengelola kontak yang mengambil daftar nomor dari buku telepon ponsel lalu menyajikannya dalam bentuk kartu vertikal yang nyaman disentuh jari. Setiap kartu menampilkan nama asli buku telepon sebagai teks sekunder, kolom ketik langsung (
\f1\i inline input
\f0\i0 ) untuk mengisi nama panggilan akrab (
\f1\i Custom Nickname
\f0\i0 ), serta label kategori (
\f1\i Tag
\f0\i0 ) seperti Keluarga, Teman, atau Bisnis yang bisa ditambah sendiri oleh pengguna. Di bagian atas halaman terdapat deretan tombol penyaring kategori beserta fitur "Pilih Semua" yang hanya mencentang kontak di dalam saringan aktif tersebut, sekaligus mengirimkan konteks kategori terpilih ke mesin AI agar nada bicara yang dihasilkan selaras dengan kelompok penerima. Halaman ketiga menampilkan antrean pengiriman secara langsung lengkap dengan bar progres, status aktivitas kontak demi kontak, serta tombol jeda (
\f1\i Pause
\f0\i0 ) dan henti darurat (
\f1\i Stop
\f0\i0 ). Sementara halaman keempat berfungsi sebagai pusat pengaturan bahasa aplikasi, pengatur rentang waktu jeda acak, sakelar simulasi mengetik, jeda istirahat per kelompok kiriman (
\f1\i batch cooldown
\f0\i0 ), serta panel penghubung akun WhatsApp biasa dan WhatsApp Business menggunakan metode kode tautan delapan digit (
\f1\i 8-Digit Pairing Code
\f0\i0 ) agar kedua akun dapat ditautkan langsung di ponsel yang sama tanpa memerlukan pemindaian kode QR.
\fs24 \cf0 \up0 \
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\sl-560\sb340\sa120\partightenfactor0

\fs40 \cf2 \up1 Keputusan Teknis Tambahan dari Sisi Perancang
\fs24 \cf0 \up0 \
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\sl-520\sa280\partightenfactor0

\fs34 \cf2 \up2 Agar cetak biru ini siap langsung dibangun tanpa menyisakan celah teknis, ada lima keputusan arsitektur di balik layar yang saya tetapkan sebagai keputusan perancang untuk kamu periksa. Pertama, untuk menjalankan protokol perangkat tertaut di dalam Android secara stabil, mesin pengirim akan menggunakan pustaka berbasis protokol multi-perangkat (seperti 
\f1\i Whatsmeow
\f0\i0  yang dikompilasi ke pustaka asli Android atau mesin ringan setara) yang dibungkus di dalam layanan latar depan (
\f1\i Android Foreground Service
\f0\i0 ) dengan satu notifikasi status aktif agar sistem operasi Android tidak mematikan koneksi saat layar ponsel terkunci. Kedua, seluruh perubahan nama panggilan dan label kategori akan disimpan di dalam basis data lokal mandiri (
\f1\i SQLite/Room Database
\f0\i0 ) di dalam aplikasi, sehingga buku telepon asli di ponselmu tetap bersih dan tidak berubah. Ketiga, logika pengiriman beruntun akan menggunakan sistem jeda dua lapis: jeda pendek selama tiga hingga enam detik disertai sinyal status 
\f1\i sedang mengetik...
\f0\i0  saat berpindah antar-balon pesan untuk satu orang yang sama, lalu disusul jeda acak panjang sesuai pengaturanmu (misalnya 25 hingga 60 detik) saat berpindah ke orang berikutnya. Keempat, jika kamu memilih mode pengiriman "Kedua Akun", mesin akan membagi beban pengiriman secara bergantian (
\f1\i round-robin
\f0\i0 ) antara akun biasa dan akun bisnis untuk menekan risiko batas pengiriman harian di masing-masing nomor. Kelima, komunikasi antara aplikasi Android dan jembatan Cloudflare milikmu akan menggunakan format data JSON terstruktur yang mengunci posisi tag 
\f2 \{Nickname\}
\f0  agar tidak pernah diterjemahkan atau dihapus oleh Groq maupun Gemini, baik saat mengolah pesan Bahasa Indonesia maupun Bahasa Turki.}