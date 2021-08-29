package com.example.ulmoto.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.ulmoto.db.models.Repair

@Dao
interface RepairDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(repair: Repair)

    @Delete
    suspend fun delete(repair: Repair)
}