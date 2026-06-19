package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaksi")
data class Transaksi(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nomorTransaksi: String,
    val tanggal: Long = System.currentTimeMillis(),
    val jenis: String, // Pemasukan, Pengeluaran
    val kategori: String,
    val nominal: Double,
    val idUnit: Int?, // nullable for general transactions
    val keterangan: String,
    val status: String = "Disetujui" // Draft, Menunggu, Disetujui, Ditolak
)
