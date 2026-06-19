package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unit_usaha")
data class UnitUsaha(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val namaUnit: String,
    val deskripsi: String,
    val status: String = "Active" // Active, Inactive
)
