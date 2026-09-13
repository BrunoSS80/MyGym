package com.mygym.android.domain.model

import java.time.DayOfWeek

data class Workout(
    val id: Long = 0,
    val title: String,
    val tag: String, // ex: "CC", "AB", "PE", "CO"
    val dayOfWeek: DayOfWeek, // Seg a Dom
    val estimatedMinutes: Int = 45,
    val colorPrimaryHex: Long = 0xFF3B82F6, // PulseBlue default
    val colorSecondaryHex: Long = 0xFF2563EB,
    val exercises: List<Exercise> = emptyList(),
    val isCompletedToday: Boolean = false
)

