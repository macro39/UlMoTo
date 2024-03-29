package com.example.ulmoto.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ulmoto.db.converters.DateConverter
import com.example.ulmoto.db.converters.StringListConverter
import com.example.ulmoto.db.dao.RecordDao
import com.example.ulmoto.db.dao.RecordWithRepairsDao
import com.example.ulmoto.db.dao.RepairDao
import com.example.ulmoto.db.models.Record
import com.example.ulmoto.db.models.Repair

@Database(entities = [Repair::class, Record::class], version = 1, exportSchema = true)
@TypeConverters(DateConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun repairDao(): RepairDao
    abstract fun recordWithRepairsDao(): RecordWithRepairsDao

    companion object {
        private const val DATABASE_NAME = "RecordDb"

        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()

            // in memory database deleted after app is killed
//            return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        }
    }
}
