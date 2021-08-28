package com.example.ulmoto.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.ulmoto.data.models.RecordWithRepairs

@Dao
interface RecordWithRepairsDao {
    @Transaction
    @Query("SELECT * FROM record WHERE recordId = :recordId")
    suspend fun getRecordWithRepairs(recordId: Long): RecordWithRepairs
}