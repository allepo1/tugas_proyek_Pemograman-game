[laporan_tugas.md](https://github.com/user-attachments/files/34257197/laporan_tugas.md)
# Laporan Penyelesaian Tugas Proyek Pemrograman Game

Repositori ini berisi proyek aplikasi Android berbasis Jetpack Compose. Laporan di bawah ini menjelaskan penyelesaian dari 3 (tiga) tugas utama yang diimplementasikan ke dalam kode.

---

### 1. Cara Mengubah Gambar Ikon dan Memasukkannya ke `Image`
Untuk mengubah gambar ikon, langkah pertama adalah memasukkan file gambar (misalnya `.png` atau `.xml` vector) ke dalam folder `app/src/main/res/drawable/` di Android Studio. Setelah itu, gambar dipanggil di dalam komponen Jetpack Compose menggunakan `painterResource`.

**Script Implementasi:**
```kotlin
Image(
    painter = painterResource(id = R.drawable.nama_file_gambar), // Memanggil gambar dari folder drawable
    contentDescription = "Ikon Produk",
    modifier = Modifier.size(64.dp)
)
```

---

### 2. Script Klik Produk dan Memunculkan Card Order (Total Harga)
Fitur ini menggunakan *State Management* (`mutableStateOf`) pada Jetpack Compose. State digunakan untuk menyimpan total harga. Ketika salah satu produk diklik, total harga akan bertambah, dan sebuah *Card* baru di bagian bawah akan muncul untuk menampilkan total harga tersebut.

**Script Implementasi:**
```kotlin
// Inisialisasi state untuk menyimpan total harga
var totalHarga by remember { mutableStateOf(0) }

Column {
    // Card Produk
    Card(
        modifier = Modifier.clickable {
            // Menambah total harga saat produk diklik
            totalHarga += 25000 
        }
    ) {
        Text("Kopi Hitam - Rp 25.000")
    }

    // Card Order muncul secara otomatis jika totalHarga lebih dari 0
    if (totalHarga > 0) {
        Card(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Total Order Anda: Rp $totalHarga")
        }
    }
}
```

---

### 3. Cara Membuat Warna Card Produk Selang-seling (Zig-zag)
Untuk membuat warna latar belakang *Card* menjadi zig-zag (selang-seling), digunakan logika matematika sederhana yaitu **modulo (`%`)** berdasarkan urutan atau *index* data. Fungsi `itemsIndexed` digunakan pada `LazyColumn` agar kita bisa mendapatkan nilai *index* dari setiap produk. Jika *index* genap, warna akan diatur menjadi A, dan jika ganjil menjadi B.

**Script Implementasi:**
```kotlin
LazyColumn {
    itemsIndexed(daftarProduk) { index, produk ->
        // Logika Zig-zag: Cek apakah index genap atau ganjil
        val warnaBackground = if (index % 2 == 0) {
            Color.LightGray // Warna untuk baris genap
        } else {
            Color.White     // Warna untuk baris ganjil
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = warnaBackground),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = produk.nama)
        }
    }
}
```
