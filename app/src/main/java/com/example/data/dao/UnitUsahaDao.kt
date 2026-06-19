package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.UnitUsaha
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitUsahaDao {
    @Query("SELECT * FROM unit_usaha")
    fun getAllUnits(): Flow<List<UnitUsaha>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: UnitUsaha)
    
    @Query("UPDATE unit_usaha SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)
}
