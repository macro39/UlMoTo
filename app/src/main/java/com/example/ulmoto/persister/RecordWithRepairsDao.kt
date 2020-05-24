package com.example.ulmoto.persister

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction


/**
 * Created by Kamil Macek on 22.5.2020.
 */
@Dao
interface RecordWithRepairsDao {
    @Transaction
    @Query("SELECT * FROM record WHERE recordId = :recordId")
    suspend fun getRecordWithRepairs(recordId: Long): RecordWithRepairs
}