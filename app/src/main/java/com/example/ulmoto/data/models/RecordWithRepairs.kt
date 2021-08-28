package com.example.ulmoto.data.models

import androidx.room.Embedded
import androidx.room.Relation


/**
 * Created by Kamil Macek on 22.5.2020.
 */
data class RecordWithRepairs(
    @Embedded val recordEntity: RecordEntity,
    @Relation(
        parentColumn = "recordId",
        entityColumn = "recordEntityId"
    )
    var repairList: List<RepairEntity>
)