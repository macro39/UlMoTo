package com.example.ulmoto.db.models

import androidx.room.Embedded
import androidx.room.Relation

data class RecordWithRepairs(
    @Embedded val record: Record,
    @Relation(
        parentColumn = "recordId",
        entityColumn = "recordEntityId"
    )
    var repairList: List<Repair>
) {
    fun getRepairsPrice(): Double {
        var sum = 0.0
        repairList.forEach {
            sum += it.price
        }
        return sum
    }
}