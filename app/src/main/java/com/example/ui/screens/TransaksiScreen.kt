package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.BumdesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaksiScreen(viewModel: BumdesViewModel) {
    val transaksiList by viewModel.allTransaksi.collectAsStateWithLifecycle()
    val units by viewModel.allUnits.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Daftar Transaksi", "Laporan BKU")

    Scaffold(
        topBar = { 
            Column {
                TopAppBar(
                    title = { Text("Keuangan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("add_trx_fab").padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Filled.Add, "Tambah Transaksi")
                }
            }
        }
    ) { padding ->
        if (selectedTab == 0) {
            LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(transaksiList) { trx ->
                    val formatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                    val dateStr = formatter.format(Date(trx.tanggal))
                    val isPemasukan = trx.jenis == "Pemasukan"
                    val valueColor = if(isPemasukan) Emerald600 else Red600
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                val nominalStr = String.format("%,.0f", trx.nominal)
                                val prefix = if(isPemasukan) "+" else "-"
                                Text(text = trx.jenis, color = valueColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                Text(text = "$prefix Rp $nominalStr", color = valueColor, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "${trx.keterangan.ifEmpty { trx.kategori }}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (transaksiList.isEmpty()) {
                    item { Text("Belum ada transaksi. Silakan tambah baru.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        } else {
            Box(modifier = Modifier.padding(padding)) {
                BukuKasUmumTab(viewModel)
            }
        }

        if (showDialog) {
            var jenis by remember { mutableStateOf("Pemasukan") }
            var kategori by remember { mutableStateOf("") }
            var nominalStr by remember { mutableStateOf("") }
            var keterangan by remember { mutableStateOf("") }
            var selectedUnit by remember { mutableStateOf<Int?>(null) }
            
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Catat Transaksi") },
                text = {
                    Column {
                        Row {
                            RadioButton(
                                selected = jenis == "Pemasukan", 
                                onClick = { jenis = "Pemasukan" },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Pemasukan", modifier = Modifier.padding(top=12.dp))
                            Spacer(Modifier.width(8.dp))
                            RadioButton(
                                selected = jenis == "Pengeluaran", 
                                onClick = { jenis = "Pengeluaran" },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Pengeluaran", modifier = Modifier.padding(top=12.dp))
                        }
                        OutlinedTextField(
                            value = nominalStr,
                            onValueChange = { nominalStr = it },
                            label = { Text("Nominal (Rp)") },
                            modifier = Modifier.fillMaxWidth().testTag("input_nominal"),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = kategori,
                            onValueChange = { kategori = it },
                            label = { Text("Kategori (Contoh: Penjualan)") },
                            modifier = Modifier.fillMaxWidth().testTag("input_kategori")
                        )
                        OutlinedTextField(
                            value = keterangan,
                            onValueChange = { keterangan = it },
                            label = { Text("Keterangan") },
                            modifier = Modifier.fillMaxWidth().testTag("input_keterangan")
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val nom = nominalStr.toDoubleOrNull() ?: 0.0
                        if (nom > 0) {
                            viewModel.addTransaksi(jenis, kategori, nom, selectedUnit, keterangan)
                            showDialog = false
                        }
                    }, modifier = Modifier.testTag("save_trx_btn"),
                       colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                    ) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Batal") }
                }
            )
        }
    }
}
