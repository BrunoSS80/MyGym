package com.mygym.android.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.mygym.android.data.local.entity.ExerciseEntity
import com.mygym.android.data.local.entity.WorkoutEntity

data class WorkoutWithExercises(
    @Embedded
    val workout: WorkoutEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val exercises: List<ExerciseEntity>
)

