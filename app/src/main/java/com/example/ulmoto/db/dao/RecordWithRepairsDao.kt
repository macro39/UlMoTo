package com.example.ulmoto.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import com.example.ulmoto.db.models.Record
import com.example.ulmoto.db.models.RecordWithRepairs
import com.example.ulmoto.db.models.Repair
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordWithRepairsDao {
    @Transaction
    @Query("SELECT * FROM record WHERE recordId = :recordId")
    fun getRecordWithRepairs(recordId: Long): Flow<RecordWithRepairs>

    @Delete
    suspend fun delete(record: Record, repairs: List<Repair>)
}