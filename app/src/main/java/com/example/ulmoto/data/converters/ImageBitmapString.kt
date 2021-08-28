package com.example.ulmoto.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ImageBitmapString {
//    @TypeConverter
//    fun bitmapToString(bitmap: Bitmap): ByteArray? {
//
//        val outStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
//        val byteArray = outStream.toByteArray()
//
//        return Base64.getEncoder().encode(byteArray)
//    }
//
//    @TypeConverter
//    fun bitmapToString(encodedString: String): Bitmap {
//        val encodeByte = Base64.getDecoder().decode(encodedString)
//        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
//    }


    @TypeConverter
    fun restoreList(listOfString: String?): List<String?>? {
        return Gson().fromJson(listOfString, object : TypeToken<List<String?>?>() {}.type)
    }

    @TypeConverter
    fun saveList(listOfString: List<String?>?): String? {
        return Gson().toJson(listOfString)
    }
}