package com.mygym.android.data.repository

import com.mygym.android.domain.model.Exercise
import com.mygym.android.domain.model.WeekProgress
import com.mygym.android.domain.model.Workout
import com.mygym.android.domain.model.WorkoutLog
import com.mygym.android.domain.util.DateUtils
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getTodayWorkout(date: LocalDate = DateUtils.getToday()): Flow<Workout?>
    fun getWorkoutForDay(dayOfWeek: DayOfWeek, date: LocalDate = DateUtils.getToday()): Flow<Workout?>
    fun getAllWorkouts(): Flow<List<Workout>>
    fun getWorkoutById(workoutId: Long, date: LocalDate = DateUtils.getToday()): Flow<Workout?>
    suspend fun saveWorkout(workout: Workout, exercises: List<Exercise>): Long
    suspend fun deleteWorkout(workoutId: Long)
    suspend fun toggleExerciseCompletion(workoutId: Long, exerciseId: Long, date: LocalDate = DateUtils.getToday())
    suspend fun completeWorkout(workoutId: Long, date: LocalDate = DateUtils.getToday())
    suspend fun uncompleteWorkout(workoutId: Long, date: LocalDate = DateUtils.getToday())
    fun getWeeklyProgress(referenceDate: LocalDate = DateUtils.getToday()): Flow<WeekProgress>
    fun getAllWorkoutLogs(): Flow<List<WorkoutLog>>
    suspend fun resetDatabaseToDefaults()
}

