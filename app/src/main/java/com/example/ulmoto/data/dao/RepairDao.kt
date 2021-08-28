package com.example.ulmoto.data.dao

import androidx.room.*
import com.example.ulmoto.data.models.Repair

@Dao
interface RepairDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(repair: Repair): Long

    @Delete
    suspend fun delete(repair: Repair)

    @Query("SELECT SUM(price) AS value FROM repair WHERE recordEntityId = :recordId")
    suspend fun getPriceCount(recordId: Long): Double
}