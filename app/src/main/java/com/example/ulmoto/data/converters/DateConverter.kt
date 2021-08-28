package com.example.ulmoto.data.converters

import androidx.room.TypeConverter
import java.util.*


/**
 * Created by Kamil Macek on 24.5.2020.
 */
class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}