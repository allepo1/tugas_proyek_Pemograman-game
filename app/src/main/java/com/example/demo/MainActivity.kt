package com.example.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// =========================================================================
// 1. MATERIAL DESIGN COLOR SYSTEM (Palet Warna Kustom Material 3)
// =========================================================================
private val CreamBackground = Color(0x000000)
private val CardTan = Color(0xFFD6BE96)
private val HeaderBrown = Color(0xFFC4A675)
private val ButtonBrown = Color(0xFF8C7355)
private val DarkBrown = Color(0xFF2E1F14)
private val MutedBrown = Color(0xFF6B5744)

// Palet warna visual topping/varian roti
private val ButterGold = Color(0xFFF7E3B5)
private val PineappleYellow = Color(0xFF000000)
private val ChocoDark = Color(0xFFB58E6D)
private val StrawberryPink = Color(0xFFF4C2C2)

// Ukuran minimum kartu di Grid agar responsif di berbagai layar
private val GridMinMenuSize = 170.dp

// Data class untuk produk roti
private data class RotiItem(
    val id: String,
    val ikon: String,
    val nama: String,
    val jenisRoti: String,
    val hargaRibuan: Int,
    val kategori: String,
    val warnaTopping: Color
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CreamBackground
                ) {
                    LiliBakeryAdaptiveApp()
                }
            }
        }
    }
}

@Composable
fun LiliBakeryAdaptiveApp() {
    var layarAktif by remember { mutableIntStateOf(1) }
    var tampilkanPopUpCheckout by remember { mutableStateOf(false) }

    // Menyimpan jumlah pesanan per ID roti agar tidak hilang saat di-scroll
    val keranjangPesanan = remember { mutableStateMapOf<String, Int>() }

    val daftarRoti = remember {
        listOf(
            RotiItem("c1", "🥐✨", "Original Croissant", "Classic Butter", 15, "Croissant", ButterGold),
            RotiItem("c2", "🥐🍍", "Pineapple Croissant", "Tropical Glaze", 15, "Croissant", PineappleYellow),
            RotiItem("c3", "🥐🍫", "Choco Croissant", "Belgian Choco", 15, "Croissant", ChocoDark),
            RotiItem("c4", "🥐🍓", "Strawberry Croissant", "Berry Filling", 15, "Croissant", StrawberryPink),
            RotiItem("b1", "🥖🧄", "Garlic Baguette", "French Artisan", 18, "Roti Lain", ButterGold),
            RotiItem("b2", "🥨🧂", "Bavarian Pretzel", "Sea Salt Crust", 16, "Roti Lain", ChocoDark),
            RotiItem("b3", "🥯🧀", "Cream Cheese Bagel", "Sesame Topped", 20, "Roti Lain", PineappleYellow),
            RotiItem("b4", "🍞🍓", "Brioche Berry", "Sweet Loaf", 22, "Roti Lain", StrawberryPink)
        )
    }

    // Menghitung total item dan total harga otomatis dari keranjang
    val totalItem = keranjangPesanan.values.sum()
    val totalHarga = daftarRoti.sumOf { roti ->
        (keranjangPesanan[roti.id] ?: 0) * roti.hargaRibuan
    }

    // =========================================================================
    // 2. ADAPTIVE CONTAINER (BoxWithConstraints)
    // Membaca lebar layar secara real-time untuk mengubah tata letak HP vs Tablet
    // =========================================================================
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val currentWidth = maxWidth
        val widthClassLabel = when {
            currentWidth < 600.dp -> "COMPACT · PONSEL"
            currentWidth < 840.dp -> "MEDIUM · TABLET KECIL"
            else -> "EXPANDED · TABLET / DESKTOP"
        }
        val isExpanded = currentWidth >= 840.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // Side Rail otomatis muncul jika layar >= 840.dp (Mode Tablet)
            if (isExpanded) {
                BakerySideRail(
                    layarAktif = layarAktif,
                    onPilihLayar = { layarAktif = it }
                )
            }

            // Konten Utama
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                NavigasiPrototype(
                    layarAktif = layarAktif,
                    widthClassLabel = widthClassLabel,
                    onPilihLayar = { layarAktif = it }
                )

                when (layarAktif) {
                    1 -> {
                        SignInScreen(
                            isExpanded = isExpanded,
                            onKeSignUp = { layarAktif = 2 },
                            onMasukMenu = { layarAktif = 3 }
                        )
                    }
                    2 -> {
                        SignUpScreen(
                            isExpanded = isExpanded,
                            onKembaliSignIn = { layarAktif = 1 },
                            onMasukMenu = { layarAktif = 3 }
                        )
                    }
                    3 -> {
                        MenuScreenAdaptive(
                            daftarRoti = daftarRoti,
                            keranjangPesanan = keranjangPesanan,
                            totalItem = totalItem,
                            totalHarga = totalHarga,
                            onTambahRoti = { id ->
                                keranjangPesanan[id] = (keranjangPesanan[id] ?: 0) + 1
                            },
                            onKurangRoti = { id ->
                                val current = keranjangPesanan[id] ?: 0
                                if (current > 0) keranjangPesanan[id] = current - 1
                            },
                            onKlikCheckout = { tampilkanPopUpCheckout = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    else -> {
                        ThankYouScreen(
                            isExpanded = isExpanded,
                            totalItem = totalItem,
                            totalHarga = totalHarga,
                            onUlangiPesanan = {
                                keranjangPesanan.clear()
                                layarAktif = 1
                            }
                        )
                    }
                }
            }
        }
    }

    // =========================================================================
    // 3. MATERIAL ALERT DIALOG (Pop-Up Checkout)
    // =========================================================================
    if (tampilkanPopUpCheckout) {
        AlertDialog(
            onDismissRequest = { tampilkanPopUpCheckout = false },
            containerColor = CreamBackground,
            shape = RoundedCornerShape(24.dp),
            title = {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HeaderBrown)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Lili Bakery",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkBrown
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "🥐🥖🥨", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Thank You For Your Order",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        color = DarkBrown
                    )
                    Text(
                        text = "Please wait a few minutes",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        color = MutedBrown
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CardTan,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Total Pesanan: $totalItem Roti", color = DarkBrown)
                            Text(
                                text = if (totalHarga > 0) "Rp ${totalHarga}.000" else "Rp 0",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkBrown
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        tampilkanPopUpCheckout = false
                        layarAktif = 4
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBrown)
                ) {
                    Text("OK, Lihat Layar Pesanan")
                }
            },
            dismissButton = {
                Button(
                    onClick = { tampilkanPopUpCheckout = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderBrown)
                ) {
                    Text("Tambah Roti Lagi", color = DarkBrown)
                }
            }
        )
    }
}

// =========================================================================
// 4. SIDE RAIL NAVIGATION (Khusus Layar Tablet / Expanded)
// =========================================================================
@Composable
private fun BakerySideRail(layarAktif: Int, onPilihLayar: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .width(110.dp)
            .fillMaxHeight()
            .background(CardTan)
            .padding(vertical = 24.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🥐\nLili",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = DarkBrown
        )
        Spacer(modifier = Modifier.height(8.dp))
        SideRailButton("1. Sign In", layarAktif == 1) { onPilihLayar(1) }
        SideRailButton("2. Sign Up", layarAktif == 2) { onPilihLayar(2) }
        SideRailButton("3. Menu", layarAktif == 3) { onPilihLayar(3) }
        SideRailButton("4. Selesai", layarAktif == 4) { onPilihLayar(4) }
    }
}

@Composable
private fun SideRailButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) ButtonBrown else HeaderBrown
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Color.White else DarkBrown,
            fontWeight = FontWeight.Bold
        )
    }
}

// =========================================================================
// 5. TOP BANNER & PROTOTYPE NAVIGATION
// =========================================================================
@Composable
private fun NavigasiPrototype(
    layarAktif: Int,
    widthClassLabel: String,
    onPilihLayar: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CardTan
            ) {
                Text(
                    text = "Mode: $widthClassLabel",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = HeaderBrown
            ) {
                Text(
                    text = "Layar $layarAktif / 4",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkBrown,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HeaderLogoBakery(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HeaderBrown),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Lili Bakery",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = DarkBrown
            )
            Text(
                text = "🥐🥖🥨🥯",
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = "Fresh Croissants, Artisan Breads & Pastries",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = DarkBrown
            )
        }
    }
}

// =========================================================================
// 6. ADAPTIVE SIGN IN & SIGN UP SCREEN (Bisa Diketik dengan OutlinedTextField)
// Di HP: 1 Kolom Atas-Bawah | Di Tablet: 2 Kolom Kiri-Kanan
// =========================================================================
@Composable
private fun SignInScreen(
    isExpanded: Boolean,
    onKeSignUp: () -> Unit,
    onMasukMenu: () -> Unit
) {
    var emailInput by remember { mutableStateOf("mahasiswa@umsida.ac.id") }
    var passwordInput by remember { mutableStateOf("12345678") }
    var infoDemoDitampilkan by remember { mutableStateOf(false) }

    val formContent: @Composable (Modifier) -> Unit = { mod ->
        Card(
            modifier = mod.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CardTan),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Please Sign In",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )
                    Button(
                        onClick = onMasukMenu,
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBrown),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text("→")
                    }
                }

                // Input Teks Interaktif (Bisa dihapus & diketik)
                InputFormInteraktif(
                    label = "E-mail Address",
                    value = emailInput,
                    onValueChange = { emailInput = it }
                )
                InputFormInteraktif(
                    label = "Password",
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    isPassword = true
                )

                Button(
                    onClick = { infoDemoDitampilkan = !infoDemoDitampilkan },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderBrown)
                ) {
                    Text(
                        text = if (infoDemoDitampilkan) "Sembunyikan Info Login" else "Lihat Info Akun Prototype",
                        color = DarkBrown
                    )
                }

                if (infoDemoDitampilkan) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CreamBackground,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Mode Prototype Aktif", fontWeight = FontWeight.Bold, color = DarkBrown)
                            Text(
                                text = "Kolom di atas bisa diketik. Klik tombol panah (→) atau tombol Sign In untuk masuk ke Menu.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedBrown
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onMasukMenu,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBrown)
                    ) {
                        Text("Sign In")
                    }
                    Button(
                        onClick = onKeSignUp,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = HeaderBrown)
                    ) {
                        Text("Sign Up", color = DarkBrown)
                    }
                }
            }
        }
    }

    if (isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderLogoBakery(modifier = Modifier.weight(1f))
            formContent(Modifier.weight(1.2f))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderLogoBakery()
            formContent(Modifier)
        }
    }
}

@Composable
private fun SignUpScreen(
    isExpanded: Boolean,
    onKembaliSignIn: () -> Unit,
    onMasukMenu: () -> Unit
) {
    var firstName by remember { mutableStateOf("Aulia") }
    var lastName by remember { mutableStateOf("Riska") }
    var email by remember { mutableStateOf("aulia@email.com") }
    var phone by remember { mutableStateOf("08123456789") }
    var password by remember { mutableStateOf("12345678") }

    val formContent: @Composable (Modifier) -> Unit = { mod ->
        Card(
            modifier = mod.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CardTan),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )
                    Button(
                        onClick = onMasukMenu,
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBrown),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text("→")
                    }
                }

                InputFormInteraktif("First name", firstName, { firstName = it })
                InputFormInteraktif("Last name", lastName, { lastName = it })
                InputFormInteraktif("E-mail Address", email, { email = it })
                InputFormInteraktif("Mobile Number", phone, { phone = it })
                InputFormInteraktif("Password", password, { password = it }, isPassword = true)

                Button(
                    onClick = onKembaliSignIn,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderBrown)
                ) {
                    Text("Kembali ke Sign In", color = DarkBrown)
                }
            }
        }
    }

    if (isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            HeaderLogoBakery(modifier = Modifier.weight(1f))
            formContent(Modifier.weight(1.3f))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderLogoBakery()
            formContent(Modifier)
        }
    }
}

// =========================================================================
// 7. ADAPTIVE MENU SCREEN DENGAN LAZYVERTICALGRID
// Kolom roti otomatis bertambah sesuai lebar layar (HP: 2 kolom, Tablet: 3-4 kolom)
// =========================================================================
@Composable
private fun MenuScreenAdaptive(
    daftarRoti: List<RotiItem>,
    keranjangPesanan: Map<String, Int>,
    totalItem: Int,
    totalHarga: Int,
    onTambahRoti: (String) -> Unit,
    onKurangRoti: (String) -> Unit,
    onKlikCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var promoDitampilkan by remember { mutableStateOf(false) }
    var kategoriDipilih by remember { mutableStateOf("Semua") }

    val rotiTampil = daftarRoti.filter {
        kategoriDipilih == "Semua" || it.kategori == kategoriDipilih
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = GridMinMenuSize),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Banner Menu (Full Width Span)
            item(span = { GridItemSpan(maxLineSpan) }) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = HeaderBrown,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Our Menu",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                            color = DarkBrown
                        )
                        Button(
                            onClick = { promoDitampilkan = !promoDitampilkan },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonBrown)
                        ) {
                            Text(if (promoDitampilkan) "Tutup Promo Bakery" else "Lihat Promo (8 Varian Roti)")
                        }

                        if (promoDitampilkan) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = CreamBackground,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "🎉 Tersedia Croissant, Baguette, Pretzel, Bagel & Brioche! Beli 3 Gratis 1 Kopi.",
                                    modifier = Modifier.padding(10.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBrown
                                )
                            }
                        }
                    }
                }
            }

            // Filter Kategori (Full Width Span)
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Semua" to "Semua", "Croissant" to "Croissant", "Roti Lain" to "Baguette+").forEach { (key, label) ->
                        Button(
                            onClick = { kategoriDipilih = key },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (kategoriDipilih == key) ButtonBrown else CardTan
                            )
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (kategoriDipilih == key) Color.White else DarkBrown
                            )
                        }
                    }
                }
            }

            // Daftar Kartu Roti (Adaptif mengikuti lebar layar)
            items(rotiTampil, key = { it.id }) { roti ->
                MenuCard(
                    roti = roti,
                    jumlahPesan = keranjangPesanan[roti.id] ?: 0,
                    onTambah = { onTambahRoti(roti.id) },
                    onKurang = { onKurangRoti(roti.id) }
                )
            }
        }

        // Tombol Checkout selalu menempel di bawah
        Button(
            onClick = onKlikCheckout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonBrown)
        ) {
            Text("Checkout Pesanan ($totalItem Item - Rp ${totalHarga}.000)")
        }
    }
}

@Composable
private fun ThankYouScreen(
    isExpanded: Boolean,
    totalItem: Int,
    totalHarga: Int,
    onUlangiPesanan: () -> Unit
) {
    val cardSelesai: @Composable (Modifier) -> Unit = { mod ->
        Card(
            modifier = mod.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CardTan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pesanan Selesai!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
                Text(
                    text = "Total Item: $totalItem\nTotal Harga: Rp ${totalHarga}.000",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MutedBrown
                )
                Button(
                    onClick = onUlangiPesanan,
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBrown)
                ) {
                    Text("Buat Pesanan Baru")
                }
            }
        }
    }

    if (isExpanded) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderLogoBakery(modifier = Modifier.weight(1f))
            cardSelesai(Modifier.weight(1f))
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderLogoBakery()
            cardSelesai(Modifier)
        }
    }
}

// =========================================================================
// 8. KOMPONEN FORM INPUT INTERAKTIF & KARTU MENU
// =========================================================================
@Composable
private fun InputFormInteraktif(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = DarkBrown
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CreamBackground,
                unfocusedContainerColor = CreamBackground,
                focusedBorderColor = ButtonBrown,
                unfocusedBorderColor = HeaderBrown,
                focusedTextColor = DarkBrown,
                unfocusedTextColor = DarkBrown
            )
        )
    }
}

@Composable
private fun MenuCard(
    roti: RotiItem,
    jumlahPesan: Int,
    onTambah: () -> Unit,
    onKurang: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = roti.warnaTopping)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = roti.ikon, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = roti.nama,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )
            Text(
                text = roti.jenisRoti,
                style = MaterialTheme.typography.bodySmall,
                color = MutedBrown
            )
            Text(
                text = "Rp ${roti.hargaRibuan}k",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onKurang,
                    enabled = jumlahPesan > 0,
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBrown)
                ) {
                    Text("-")
                }
                Text(
                    text = "$jumlahPesan",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
                Button(
                    onClick = onTambah,
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBrown)
                ) {
                    Text("+")
                }
            }
        }
    }
}

// =========================================================================
// 9. MULTI-SCREEN PREVIEW (HP, MEDIUM, DAN TABLET)
// =========================================================================
@Preview(name = "1. Ponsel · Compact", showBackground = true, widthDp = 393, heightDp = 850)
@Composable
private fun LiliBakeryPhonePreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = CreamBackground) {
            LiliBakeryAdaptiveApp()
        }
    }
}

@Preview(name = "2. Medium · 700dp", showBackground = true, widthDp = 700, heightDp = 900)
@Composable
private fun LiliBakeryMediumPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = CreamBackground) {
            LiliBakeryAdaptiveApp()
        }
    }
}

@Preview(name = "3. Tablet · Expanded", showBackground = true, widthDp = 1000, heightDp = 800)
@Composable
private fun LiliBakeryTabletPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = CreamBackground) {
            LiliBakeryAdaptiveApp()
        }
    }
}