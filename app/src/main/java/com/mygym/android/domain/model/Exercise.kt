package com.mygym.android.domain.model

data class Exercise(
    val id: Long = 0,
    val workoutId: Long = 0,
    val name: String,
    val sets: Int = 3,
    val reps: String = "10-12",
    val weightKg: Double? = null,
    val durationMinutes: Int? = null,
    val restSeconds: Int = 60,
    val notes: String = "",
    val orderIndex: Int = 0,
    val isCompleted: Boolean = false,
    // Campos de meta (goal) para o novo estilo "Treino do dia"
    val targetReps: String = "",
    val targetWeightKg: Double? = null
)

