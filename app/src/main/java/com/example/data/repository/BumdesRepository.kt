package com.example.data.repository

import com.example.data.dao.TransaksiDao
import com.example.data.dao.UnitUsahaDao
import com.example.data.dao.UserDao
import com.example.data.entity.Transaksi
import com.example.data.entity.UnitUsaha
import com.example.data.entity.User
import kotlinx.coroutines.flow.Flow

class BumdesRepository(
    private val userDao: UserDao,
    private val unitUsahaDao: UnitUsahaDao,
    private val transaksiDao: TransaksiDao
) {
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val allUnits: Flow<List<UnitUsaha>> = unitUsahaDao.getAllUnits()
    val allTransaksi: Flow<List<Transaksi>> = transaksiDao.getAllTransaksi()
    
    val totalPemasukan: Flow<Double?> = transaksiDao.getTotalPemasukan()
    val totalPengeluaran: Flow<Double?> = transaksiDao.getTotalPengeluaran()

    suspend fun getUserByUsername(username: String) = userDao.getUserByUsername(username)
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun insertUnit(unit: UnitUsaha) = unitUsahaDao.insertUnit(unit)
    suspend fun updateUnitStatus(id: Int, status: String) = unitUsahaDao.updateStatus(id, status)

    suspend fun insertTransaksi(transaksi: Transaksi) = transaksiDao.insertTransaksi(transaksi)
    suspend fun updateTransaksiStatus(id: Int, status: String) = transaksiDao.updateStatus(id, status)
}
