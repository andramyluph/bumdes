package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.entity.User
import com.example.data.entity.UnitUsaha
import com.example.data.entity.Transaksi
import com.example.data.dao.UserDao
import com.example.data.dao.UnitUsahaDao
import com.example.data.dao.TransaksiDao

@Database(entities = [User::class, UnitUsaha::class, Transaksi::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun unitUsahaDao(): UnitUsahaDao
    abstract fun transaksiDao(): TransaksiDao
}
