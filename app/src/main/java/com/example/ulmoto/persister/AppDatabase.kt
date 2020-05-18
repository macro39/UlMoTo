package com.example.ulmoto.persister

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Created by Kamil Macek on 17.5.2020.
 */
@Database(entities = [RecordEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao

    companion object {
        private const val DATABASE_NAME = "RecordDatabase"

        fun create(context: Context): AppDatabase {
//            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
            return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        }
    }
}
