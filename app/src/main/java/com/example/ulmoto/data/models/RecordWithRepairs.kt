package com.example.ulmoto.data.models

import androidx.room.Embedded
import androidx.room.Relation

data class RecordWithRepairs(
    @Embedded val record: Record,
    @Relation(
        parentColumn = "recordId",
        entityColumn = "recordEntityId"
    )
    var repairList: List<Repair>
)