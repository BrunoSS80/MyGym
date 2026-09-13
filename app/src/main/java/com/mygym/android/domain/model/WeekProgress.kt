package com.mygym.android.domain.model

import java.time.LocalDate

data class DayProgress(
    val date: LocalDate,
    val dayOfWeekLetter: String, // "D", "S", "T", "Q", "Q", "S", "S"
    val dayNumber: Int,
    val isToday: Boolean,
    val hasCompletedWorkout: Boolean,
    val hasScheduledWorkout: Boolean
)

data class WeekProgress(
    val monthYearText: String, // ex: "Setembro 2026"
    val days: List<DayProgress>,
    val activeStreakDays: Int,
    val weeklyGoalCompleted: Int,
    val weeklyGoalTotal: Int = 5
)

