package com.example.ulmoto.data.dao

import androidx.room.*
import com.example.ulmoto.data.models.RepairEntity


/**
 * Created by Kamil Macek on 23.5.2020.
 */
@Dao
interface RepairDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(repairEntity: RepairEntity): Long

    @Delete
    suspend fun delete(repairEntity: RepairEntity)

    @Query("SELECT SUM(price) AS value FROM repair WHERE recordEntityId = :recordId")
    suspend fun getPriceCount(recordId: Long): Double
}