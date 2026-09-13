package com.mygym.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val tag: String,
    val dayOfWeek: DayOfWeek,
    val estimatedMinutes: Int = 45,
    val colorPrimaryHex: Long = 0xFF3B82F6,
    val colorSecondaryHex: Long = 0xFF2563EB,
    val orderIndex: Int = 0
)

