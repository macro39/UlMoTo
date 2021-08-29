package com.example.ulmoto.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ulmoto.db.models.Record
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: Record)

    @Query("SELECT * FROM record")
    fun getAll(): Flow<List<Record>>

    @Query("SELECT * FROM record WHERE firstName LIKE '%' || :firstName || '%'")
    fun getAllByFirstName(firstName: String): Flow<List<Record>>

    @Query("SELECT * FROM record WHERE lastName LIKE '%' || :lastName || '%'")
    fun getAllByLastName(lastName: String): Flow<List<Record>>

    @Query("SELECT * FROM record WHERE licencePlate LIKE '%' || :licencePlate || '%'")
    fun getAllByLicencePlate(licencePlate: String): Flow<List<Record>>

    @Query("SELECT EXISTS(SELECT * FROM record WHERE licencePlate = :licencePlate)")
    suspend fun existsByLicencePlate(licencePlate: String): Boolean
}