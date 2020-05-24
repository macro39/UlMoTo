package com.example.ulmoto.persister

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Kamil Macek on 17.5.2020.
 */
@Entity(tableName = "record")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val recordId: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val licencePlate: String = "",
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) var imageFirst: ByteArray? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) var imageSecond: ByteArray? = null
)