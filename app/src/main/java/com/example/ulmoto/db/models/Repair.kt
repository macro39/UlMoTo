package com.example.ulmoto.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "repair")
data class Repair(
    @PrimaryKey(autoGenerate = true) val repairId: Int = 0,
    var recordEntityId: Long = 0,
    val description: String = "",
    val dateOfRepair: Date = Calendar.getInstance().time,
    val price: Double = 0.0,
    var images: List<String> = listOf()
)