package com.example.ulmoto.persister

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


/**
 * Created by Kamil Macek on 23.5.2020.
 */
@Dao
interface RepairDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(repairEntity: RepairEntity): Long

    @Query("SELECT SUM(price) AS value FROM repair WHERE recordEntityId = :recordId")
    suspend fun getPriceCount(recordId: Long): Double
}