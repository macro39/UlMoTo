package com.example.ulmoto.persister

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


/**
 * Created by Kamil Macek on 17.5.2020.
 */
@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: RecordEntity): Long

    @Query("SELECT * FROM record")
    suspend fun getAll(): List<RecordEntity>

    @Query("SELECT * FROM record WHERE firstName LIKE '%' || :firstName || '%'")
    suspend fun getAllByFirstName(firstName: String): List<RecordEntity>

    @Query("SELECT * FROM record WHERE lastName LIKE '%' || :lastName || '%'")
    suspend fun getAllByLastName(lastName: String): List<RecordEntity>

    @Query("SELECT * FROM record WHERE licenceNumber LIKE '%' || :licenceNumber || '%'")
    suspend fun getAllByLicenceNumber(licenceNumber: String): List<RecordEntity>
}