package com.example.studysmart.data.local

import androidx.room.TypeConverter

class ColorListConverter {

    @TypeConverter
    fun fromColorListToString(list: List<Int>): String {

        return list.joinToString(",") {
            it.toString()
        }
    }

    @TypeConverter
    fun fromStringToColorList(colorString: String): List<Int> {

        return colorString.split(",").map {
            it.toInt()
        }
    }
}