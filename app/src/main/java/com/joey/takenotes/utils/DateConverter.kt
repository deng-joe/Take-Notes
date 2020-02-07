package com.joey.takenotes.utils

import android.text.format.DateFormat
import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    companion object {

        @TypeConverter
        @JvmStatic
        fun toDate(timestamp: Long?) = if (timestamp == null) null else Date(timestamp)

        @TypeConverter
        @JvmStatic
        fun toTimestamp(date: Date?) = date?.time

        fun dateFormat(date: Date) = DateFormat.format("MMM dd, yyyy   HH:mm", date) as String
    }

}
