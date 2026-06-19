package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.BumdesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: BumdesViewModel) {
    val totalPemasukan by viewModel.totalPemasukan.collectAsStateWithLifecycle()
    val totalPengeluaran by viewModel.totalPengeluaran.collectAsStateWithLifecycle()
    val allUnits by viewModel.allUnits.collectAsStateWithLifecycle()
    val allTransaksi by viewModel.allTransaksi.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    
    val labaBersih = (totalPemasukan ?: 0.0) - (totalPengeluaran ?: 0.0)
    val kas = labaBersih
    
    val currentUser = allUsers.firstOrNull() ?: com.example.data.entity.User(nama = "Admin BUMDes", username = "admin", passwordHash = "", role = "Pamong Desa")
    
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        val currentTheme by viewModel.themePreference.collectAsStateWithLifecycle()
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Pilih Tema", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    com.example.viewmodel.ThemeMode.values().forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemePreference(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = currentTheme == mode,
                                onClick = {
                                    viewModel.setThemePreference(mode)
                                    showThemeDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Emerald600)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when(mode) {
                                  com.example.viewmodel.ThemeMode.SYSTEM -> "Ikuti Sistem"
                                  com.example.viewmodel.ThemeMode.LIGHT -> "Terang"
                                  com.example.viewmodel.ThemeMode.DARK -> "Gelap"
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Tutup", color = Emerald600)
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(bottom = padding.calculateBottomPadding() + 16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                HeaderSection(kas, currentUser, onThemeActionClick = { showThemeDialog = true })
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                PerformanceGrid(
                    pemasukan = totalPemasukan ?: 0.0,
                    pengeluaran = totalPengeluaran ?: 0.0,
                    labaBersih = labaBersih,
                    transaksiCount = allTransaksi.size
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                UnitUsahaSection(allUnits)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                AktivitasSection(allTransaksi.take(3))
            }
        }
    }
}

@Composable
fun HeaderSection(kas: Double, user: com.example.data.entity.User, onThemeActionClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            val kasStr = String.format("%,.0f", kas)
            val initials = user.nama.split(" ").take(2).map { it.firstOrNull()?.uppercase() ?: "" }.joinToString("")
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        user.role.ifEmpty { "Pamong Desa" },
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.sp
                    )
                    Text(
                        user.nama,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.clickable { onThemeActionClick() }) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials.take(2), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                            .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.05f))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            "Total Saldo Kas",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Rp $kasStr",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "AKTIF",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PerformanceGrid(pemasukan: Double, pengeluaran: Double, labaBersih: Double, transaksiCount: Int) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        val pemasukanStr = String.format("%,.0f", pemasukan)
        val pengeluaranStr = String.format("%,.0f", pengeluaran)
        val labaStr = String.format("%,.0f", labaBersih)
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Pendapatan",
                value = "Rp $pemasukanStr",
                valueColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.primary,
                iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null, tint = it, modifier = Modifier.size(16.dp))
            }
            MetricCard(
                title = "Pengeluaran",
                value = "Rp $pengeluaranStr",
                valueColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.error,
                iconBgColor = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = it, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Laba Bersih",
                value = "+Rp $labaStr",
                valueColor = MaterialTheme.colorScheme.primary,
                iconColor = MaterialTheme.colorScheme.primary,
                iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = it, modifier = Modifier.size(16.dp))
            }
            MetricCard(
                title = "Transaksi",
                value = "$transaksiCount Total",
                valueColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.secondary,
                iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = it, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    valueColor: Color,
    iconColor: Color,
    iconBgColor: Color,
    modifier: Modifier = Modifier,
    icon: @Composable (Color) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                icon(iconColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title.uppercase(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                color = valueColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun UnitUsahaSection(units: List<com.example.data.entity.UnitUsaha>) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Unit Usaha", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Lihat Semua", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            val listToRender = if (units.isEmpty()) {
               listOf(com.example.data.entity.UnitUsaha(1, "Belum Ada Unit", "Tambahkan Unit Usaha", "Active"))
            } else units
            
            items(listToRender) { unit ->
                val isPrimary = unit.id % 2 != 0 // Alternate colors
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    ),
                    border = if (isPrimary) null else BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                    modifier = Modifier.width(140.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            unit.namaUnit,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isPrimary) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            unit.status,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(if (isPrimary) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.6f)
                                    .clip(CircleShape)
                                    .background(if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AktivitasSection(transaksi: List<com.example.data.entity.Transaksi>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Aktivitas Terbaru", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 4.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                if(transaksi.isEmpty()) {
                   Text("Belum ada transaksi", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
                transaksi.forEach { trx ->
                    val isPemasukan = trx.jenis == "Pemasukan"
                    val iconColor = if (isPemasukan) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    val iconBgColor = if (isPemasukan) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                    val valueColor = if (isPemasukan) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    val sign = if (isPemasukan) "+" else "-"
                    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val time = formatter.format(Date(trx.tanggal))
                    
                    val trxNominalStr = String.format("%,.0f", trx.nominal)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(iconBgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if(isPemasukan) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                val trxText = trx.keterangan.ifEmpty { trx.kategori }
                                Text(trxText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("${trx.kategori} • $time", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Text(
                            "$sign$trxNominalStr",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = valueColor
                        )
                    }
                }
            }
        }
    }
}
