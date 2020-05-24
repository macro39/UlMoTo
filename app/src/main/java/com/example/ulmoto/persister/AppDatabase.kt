package com.example.ulmoto.persister

import android.content.Context
import androidx.room.*

/**
 * Created by Kamil Macek on 17.5.2020.
 */
@Database(entities = [RepairEntity::class, RecordEntity::class], version = 1, exportSchema = true)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun repairDao(): RepairDao
    abstract fun recordWithRepairsDao(): RecordWithRepairsDao

    companion object {
        private const val DATABASE_NAME = "RecordDatabase"

        fun create(context: Context): AppDatabase {
//            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
            return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        }
    }
}
