package com.joey.takenotes.utils

import android.text.format.DateFormat
import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    companion object {

        @TypeConverter
        @JvmStatic
        fun toDate(timestamp: Long?): Date? {
            return if (timestamp == null) null else Date(timestamp)
        }

        @TypeConverter
        @JvmStatic
        fun toTimestamp(date: Date?): Long? {
            return date?.time
        }

        fun dateFormat(date: Date): String {
            return DateFormat.format("dd MMM, yyyy    HH:mm", date) as String
        }
    }
}
