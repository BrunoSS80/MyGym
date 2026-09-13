package com.mygym.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutId: Long,
    val workoutTitle: String,
    val workoutTag: String,
    val date: LocalDate,
    val completedAtTimestamp: Long = System.currentTimeMillis(),
    val totalExercises: Int = 0,
    val completedExercises: Int = 0
)

