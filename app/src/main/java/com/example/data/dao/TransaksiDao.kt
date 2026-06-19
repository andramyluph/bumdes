package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.Transaksi
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaksiDao {
    @Query("SELECT * FROM transaksi ORDER BY tanggal DESC")
    fun getAllTransaksi(): Flow<List<Transaksi>>

    @Query("SELECT SUM(nominal) FROM transaksi WHERE jenis = 'Pemasukan' AND status = 'Disetujui'")
    fun getTotalPemasukan(): Flow<Double?>
    
    @Query("SELECT SUM(nominal) FROM transaksi WHERE jenis = 'Pengeluaran' AND status = 'Disetujui'")
    fun getTotalPengeluaran(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaksi(transaksi: Transaksi)
    
    @Query("UPDATE transaksi SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)
}
