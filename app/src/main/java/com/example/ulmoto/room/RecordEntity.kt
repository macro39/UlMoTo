package com.example.ulmoto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Kamil Macek on 17.5.2020.
 */
@Entity(tableName = "record")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val registrationBookNumber: String = "",
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val imageFirst: ByteArray? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val imageSecond: ByteArray? = null
)