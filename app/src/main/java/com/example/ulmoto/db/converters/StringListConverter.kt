package com.example.ulmoto.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringListConverter {

    @TypeConverter
    fun restoreList(listOfStrings: String?): List<String?>? {
        return Gson().fromJson(listOfStrings, object : TypeToken<List<String?>?>() {}.type)
    }

    @TypeConverter
    fun saveList(listOfString: List<String?>?): String? {
        return Gson().toJson(listOfString)
    }
}