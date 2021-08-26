package com.example.ulmoto.persister

import androidx.room.*


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