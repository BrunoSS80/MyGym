package com.mygym.android.data.repository

import com.mygym.android.data.local.dao.WorkoutDao
import com.mygym.android.data.local.dao.WorkoutLogDao
import com.mygym.android.data.local.entity.ExerciseEntity
import com.mygym.android.data.local.entity.ExerciseLogEntity
import com.mygym.android.data.local.entity.WorkoutEntity
import com.mygym.android.data.local.entity.WorkoutLogEntity
import com.mygym.android.data.local.relation.WorkoutWithExercises
import com.mygym.android.domain.model.Exercise
import com.mygym.android.domain.model.WeekProgress
import com.mygym.android.domain.model.Workout
import com.mygym.android.domain.model.WorkoutLog
import com.mygym.android.domain.util.DateUtils
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutRepositoryImpl(
    private val workoutDao: WorkoutDao,
    private val workoutLogDao: WorkoutLogDao
) : WorkoutRepository {

    override fun getTodayWorkout(date: LocalDate): Flow<Workout?> {
        val dayOfWeek = date.dayOfWeek
        return getWorkoutForDay(dayOfWeek, date)
    }

    override fun getWorkoutForDay(dayOfWeek: DayOfWeek, date: LocalDate): Flow<Workout?> {
        return workoutDao.getWorkoutsForDay(dayOfWeek).flatMapLatest { list ->
            val first = list.firstOrNull() ?: return@flatMapLatest flowOf(null)
            combine(
                workoutLogDao.isWorkoutCompletedOnDate(first.workout.id, date),
                workoutLogDao.getCompletedExerciseIdsForDate(first.workout.id, date)
            ) { isWorkoutCompleted, completedExIds ->
                mapToDomain(first, isWorkoutCompleted, completedExIds.toSet())
            }
        }
    }

    override fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkoutsWithExercises().map { list ->
            list.map { mapToDomain(it, isCompleted = false, completedExerciseIds = emptySet()) }
        }
    }

    override fun getWorkoutById(workoutId: Long, date: LocalDate): Flow<Workout?> {
        return workoutDao.getWorkoutById(workoutId).flatMapLatest { relation ->
            if (relation == null) return@flatMapLatest flowOf(null)
            combine(
                workoutLogDao.isWorkoutCompletedOnDate(workoutId, date),
                workoutLogDao.getCompletedExerciseIdsForDate(workoutId, date)
            ) { isCompleted, completedExIds ->
                mapToDomain(relation, isCompleted, completedExIds.toSet())
            }
        }
    }

    override suspend fun saveWorkout(workout: Workout, exercises: List<Exercise>): Long {
        val workoutEntity = WorkoutEntity(
            id = workout.id,
            title = workout.title,
            tag = workout.tag,
            dayOfWeek = workout.dayOfWeek,
            estimatedMinutes = workout.estimatedMinutes,
            colorPrimaryHex = workout.colorPrimaryHex,
            colorSecondaryHex = workout.colorSecondaryHex,
            orderIndex = workout.dayOfWeek.value
        )
        val exerciseEntities = exercises.mapIndexed { index, ex ->
            ExerciseEntity(
                id = ex.id,
                workoutId = workout.id,
                name = ex.name,
                sets = ex.sets,
                reps = ex.reps,
                weightKg = ex.weightKg,
                durationMinutes = ex.durationMinutes,
                restSeconds = ex.restSeconds,
                notes = ex.notes,
                orderIndex = index,
                targetReps = ex.targetReps,
                targetWeightKg = ex.targetWeightKg
            )
        }
        return workoutDao.saveWorkoutWithExercises(workoutEntity, exerciseEntities)
    }

    override suspend fun deleteWorkout(workoutId: Long) {
        workoutDao.deleteWorkoutById(workoutId)
    }

    override suspend fun toggleExerciseCompletion(workoutId: Long, exerciseId: Long, date: LocalDate) {
        val completedIds = workoutLogDao.getCompletedExerciseIdsForDate(workoutId, date).firstOrNull() ?: emptyList()
        val isCompleted = completedIds.contains(exerciseId)
        workoutLogDao.toggleExerciseLog(workoutId, exerciseId, date, isCompleted)
    }

    override suspend fun completeWorkout(workoutId: Long, date: LocalDate) {
        val workoutWithExercises = workoutDao.getWorkoutByIdDirect(workoutId) ?: return

        // 1. Inserir log do treino
        workoutLogDao.insertWorkoutLog(
            WorkoutLogEntity(
                workoutId = workoutId,
                workoutTitle = workoutWithExercises.workout.title,
                workoutTag = workoutWithExercises.workout.tag,
                date = date,
                completedAtTimestamp = System.currentTimeMillis(),
                totalExercises = workoutWithExercises.exercises.size,
                completedExercises = workoutWithExercises.exercises.size
            )
        )

        // 2. Marcar todos os exercícios como concluídos para a data
        workoutWithExercises.exercises.forEach { exercise ->
            workoutLogDao.insertExerciseLog(
                ExerciseLogEntity(
                    workoutId = workoutId,
                    exerciseId = exercise.id,
                    date = date,
                    isCompleted = true
                )
            )
        }
    }

    override suspend fun uncompleteWorkout(workoutId: Long, date: LocalDate) {
        workoutLogDao.deleteWorkoutLog(workoutId, date)
    }

    override fun getWeeklyProgress(referenceDate: LocalDate): Flow<WeekProgress> {
        return combine(
            workoutLogDao.getCompletedDates(),
            workoutDao.getAllWorkoutsWithExercises()
        ) { completedDatesList, workouts ->
            val completedDatesSet = completedDatesList.toSet()
            val scheduledDaysSet = workouts.map { it.workout.dayOfWeek }.toSet()
            DateUtils.buildWeekProgress(
                referenceDate = referenceDate,
                completedDates = completedDatesSet,
                scheduledDaysOfWeek = scheduledDaysSet,
                weeklyGoalTotal = if (scheduledDaysSet.isNotEmpty()) scheduledDaysSet.size else 5
            )
        }
    }

    override fun getAllWorkoutLogs(): Flow<List<WorkoutLog>> {
        return workoutLogDao.getAllWorkoutLogs().map { logs ->
            logs.map { entity ->
                WorkoutLog(
                    id = entity.id,
                    workoutId = entity.workoutId,
                    workoutTitle = entity.workoutTitle,
                    workoutTag = entity.workoutTag,
                    date = entity.date,
                    completedAtTimestamp = entity.completedAtTimestamp,
                    totalExercises = entity.totalExercises,
                    completedExercises = entity.completedExercises
                )
            }
        }
    }

    override suspend fun resetDatabaseToDefaults() {
        // Se precisar repovoar
        val workouts = workoutDao.getAllWorkoutsWithExercises().firstOrNull() ?: emptyList()
        workouts.forEach {
            workoutDao.deleteWorkoutById(it.workout.id)
        }
    }

    private fun mapToDomain(
        relation: WorkoutWithExercises,
        isCompleted: Boolean,
        completedExerciseIds: Set<Long>
    ): Workout {
        return Workout(
            id = relation.workout.id,
            title = relation.workout.title,
            tag = relation.workout.tag,
            dayOfWeek = relation.workout.dayOfWeek,
            estimatedMinutes = relation.workout.estimatedMinutes,
            colorPrimaryHex = relation.workout.colorPrimaryHex,
            colorSecondaryHex = relation.workout.colorSecondaryHex,
            exercises = relation.exercises.sortedBy { it.orderIndex }.map { ex ->
                Exercise(
                    id = ex.id,
                    workoutId = ex.workoutId,
                    name = ex.name,
                    sets = ex.sets,
                    reps = ex.reps,
                    weightKg = ex.weightKg,
                    durationMinutes = ex.durationMinutes,
                    restSeconds = ex.restSeconds,
                    notes = ex.notes,
                    orderIndex = ex.orderIndex,
                    isCompleted = completedExerciseIds.contains(ex.id),
                    targetReps = ex.targetReps,
                    targetWeightKg = ex.targetWeightKg
                )
            },
            isCompletedToday = isCompleted
        )
    }
}

