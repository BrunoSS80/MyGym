package com.mygym.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mygym.android.data.local.entity.ExerciseLogEntity
import com.mygym.android.data.local.entity.WorkoutLogEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLogEntity): Long

    @Query("SELECT * FROM workout_logs ORDER BY date DESC, completedAtTimestamp DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getLogsBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE date = :date")
    fun getLogsForDate(date: LocalDate): Flow<List<WorkoutLogEntity>>

    @Query("SELECT DISTINCT date FROM workout_logs ORDER BY date DESC")
    fun getCompletedDates(): Flow<List<LocalDate>>

    @Query("SELECT COUNT(*) > 0 FROM workout_logs WHERE workoutId = :workoutId AND date = :date")
    fun isWorkoutCompletedOnDate(workoutId: Long, date: LocalDate): Flow<Boolean>

    @Query("DELETE FROM workout_logs WHERE workoutId = :workoutId AND date = :date")
    suspend fun deleteWorkoutLog(workoutId: Long, date: LocalDate)

    // Exercise Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(log: ExerciseLogEntity): Long

    @Query("SELECT * FROM exercise_logs WHERE workoutId = :workoutId AND date = :date")
    fun getExerciseLogsForDate(workoutId: Long, date: LocalDate): Flow<List<ExerciseLogEntity>>

    @Query("DELETE FROM exercise_logs WHERE workoutId = :workoutId AND exerciseId = :exerciseId AND date = :date")
    suspend fun deleteExerciseLog(workoutId: Long, exerciseId: Long, date: LocalDate)

    @Query("SELECT exerciseId FROM exercise_logs WHERE workoutId = :workoutId AND date = :date AND isCompleted = 1")
    fun getCompletedExerciseIdsForDate(workoutId: Long, date: LocalDate): Flow<List<Long>>

    @Transaction
    suspend fun toggleExerciseLog(workoutId: Long, exerciseId: Long, date: LocalDate, isCurrentlyCompleted: Boolean) {
        if (isCurrentlyCompleted) {
            deleteExerciseLog(workoutId, exerciseId, date)
        } else {
            insertExerciseLog(
                ExerciseLogEntity(
                    workoutId = workoutId,
                    exerciseId = exerciseId,
                    date = date,
                    isCompleted = true
                )
            )
        }
    }
}

