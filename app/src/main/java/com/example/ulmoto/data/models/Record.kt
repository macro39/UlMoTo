package com.example.ulmoto.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record")
data class Record(
    @PrimaryKey(autoGenerate = true) val recordId: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    var licencePlate: String = "",
    var telephone: String? = null,
    var imageFirst: String? = null,
    var imageSecond: String? = null,
    var images: List<String> = listOf()
)