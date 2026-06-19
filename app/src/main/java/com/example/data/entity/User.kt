package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val username: String,
    val passwordHash: String, // Storing hash/mock in client
    val role: String, // Admin, Direktur, Bendahara, Pengelola_Unit
    val status: String = "Active"
)
