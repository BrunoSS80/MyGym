package com.mygym.android.data.local.converter

import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalDate

class DateConverters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromDayOfWeek(dayOfWeek: DayOfWeek?): String? {
        return dayOfWeek?.name
    }

    @TypeConverter
    fun toDayOfWeek(name: String?): DayOfWeek? {
        return name?.let { DayOfWeek.valueOf(it) }
    }
}

