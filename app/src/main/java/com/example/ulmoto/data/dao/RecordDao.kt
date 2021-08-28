package com.example.ulmoto.data.dao

import androidx.room.*
import com.example.ulmoto.data.models.RecordEntity


/**
 * Created by Kamil Macek on 17.5.2020.
 */
@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: RecordEntity): Long

    @Delete
    suspend fun delete(record: RecordEntity)

    @Query("SELECT * FROM record")
    suspend fun getAll(): List<RecordEntity>

    @Query("SELECT * FROM record WHERE recordId = :id")
    suspend fun getById(id: Long): RecordEntity

    @Query("SELECT * FROM record WHERE firstName LIKE '%' || :firstName || '%'")
    suspend fun getAllByFirstName(firstName: String): List<RecordEntity>

    @Query("SELECT * FROM record WHERE lastName LIKE '%' || :lastName || '%'")
    suspend fun getAllByLastName(lastName: String): List<RecordEntity>

    @Query("SELECT * FROM record WHERE licencePlate LIKE '%' || :licencePlate || '%'")
    suspend fun getAllByLicencePlate(licencePlate: String): List<RecordEntity>
}