package com.mygym.android.domain.model

import java.time.LocalDate

data class WorkoutLog(
    val id: Long = 0,
    val workoutId: Long,
    val workoutTitle: String,
    val workoutTag: String,
    val date: LocalDate,
    val completedAtTimestamp: Long,
    val totalExercises: Int,
    val completedExercises: Int
)

