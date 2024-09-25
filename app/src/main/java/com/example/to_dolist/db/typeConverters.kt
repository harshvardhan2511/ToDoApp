package com.example.to_dolist.db

import androidx.room.TypeConverter
import java.util.Date

class typeConverters {

    @TypeConverter
    fun fromDate(date : Date) : Long{
        return date.time
    }

    @TypeConverter
    fun toDate(time : Long) : Date{
        return Date(time)
    }

}