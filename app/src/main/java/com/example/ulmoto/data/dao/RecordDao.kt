package com.example.ulmoto.data.dao

import androidx.room.*
import com.example.ulmoto.data.models.Record

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: Record): Long

    @Delete
    suspend fun delete(record: Record)

    @Query("SELECT * FROM record")
    suspend fun getAll(): List<Record>

    @Query("SELECT * FROM record WHERE recordId = :id")
    suspend fun getById(id: Long): Record

    @Query("SELECT * FROM record WHERE firstName LIKE '%' || :firstName || '%'")
    suspend fun getAllByFirstName(firstName: String): List<Record>

    @Query("SELECT * FROM record WHERE lastName LIKE '%' || :lastName || '%'")
    suspend fun getAllByLastName(lastName: String): List<Record>

    @Query("SELECT * FROM record WHERE licencePlate LIKE '%' || :licencePlate || '%'")
    suspend fun getAllByLicencePlate(licencePlate: String): List<Record>
}