package com.mygym.android.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId")]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutId: Long,
    val name: String,
    val sets: Int = 3,
    val reps: String = "10-12",
    val weightKg: Double? = null,
    val durationMinutes: Int? = null,
    val restSeconds: Int = 60,
    val notes: String = "",
    val orderIndex: Int = 0,
    val targetReps: String = "",
    val targetWeightKg: Double? = null
)

