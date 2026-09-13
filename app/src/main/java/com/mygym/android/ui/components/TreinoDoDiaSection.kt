package com.mygym.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mygym.android.domain.model.Exercise
import com.mygym.android.domain.model.Workout
import com.mygym.android.ui.theme.GymSuccess
import com.mygym.android.ui.theme.MyGymTheme
import com.mygym.android.ui.theme.PulseBlue
import com.mygym.android.ui.theme.PulseBorder
import com.mygym.android.ui.theme.PulseSurface
import com.mygym.android.ui.theme.PulseSurfaceVariant
import com.mygym.android.ui.theme.PulseTextMuted
import com.mygym.android.ui.theme.PulseTextPrimary
import com.mygym.android.ui.theme.PulseTextSecondary
import java.time.DayOfWeek

/**
 * Seção "Treino do dia" com o novo estilo expandido:
 * lista os exercícios individualmente com campos Normal / Meta e botão de toggle circular.
 */
@Composable
fun TreinoDoDiaSection(
    headerDateText: String,
    workout: Workout,
    onToggleExercise: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = workout.exercises.count { it.isCompleted }
    val totalCount = workout.exercises.size

    Column(modifier = modifier.fillMaxWidth()) {
        // ---- Cabeçalho da seção ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Treino do dia",
                color = PulseTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$completedCount/$totalCount",
                color = PulseTextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = headerDateText,
            color = PulseTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---- Card com lista de exercícios ----
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(PulseSurface)
                .border(width = 1.dp, color = PulseBorder, shape = RoundedCornerShape(20.dp))
        ) {
            workout.exercises.forEachIndexed { index, exercise ->
                TreinoDoDiaExerciseRow(
                    exercise = exercise,
                    onToggle = { onToggleExercise(exercise.id) }
                )
                if (index < workout.exercises.lastIndex) {
                    HorizontalDivider(
                        color = PulseBorder,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TreinoDoDiaExerciseRow(
    exercise: Exercise,
    onToggle: () -> Unit
) {
    // Constrói as strings de exibição
    val normalText = buildNormalText(exercise)
    val metaText = buildMetaText(exercise)
    val seriesText = buildSeriesText(exercise)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ---- Info do exercício ----
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                color = if (exercise.isCompleted) PulseTextMuted else PulseTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            if (normalText.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = normalText,
                    color = PulseTextMuted,
                    fontSize = 12.sp
                )
            }

            if (metaText.isNotBlank()) {
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = metaText,
                    color = PulseBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (seriesText.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = seriesText,
                    color = PulseTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // ---- Botão toggle circular ----
        IconButton(
            onClick = onToggle,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    if (exercise.isCompleted) GymSuccess else PulseSurfaceVariant
                )
        ) {
            if (exercise.isCompleted) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Concluído",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .border(2.dp, PulseBlue.copy(alpha = 0.6f), CircleShape)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Helpers para montar as strings de exibição
// ---------------------------------------------------------------------------

private fun buildNormalText(exercise: Exercise): String {
    val parts = mutableListOf<String>()
    if (exercise.reps.isNotBlank()) parts.add("${exercise.reps}")
    if (exercise.weightKg != null && exercise.weightKg > 0) {
        val kg = if (exercise.weightKg % 1.0 == 0.0)
            exercise.weightKg.toInt().toString()
        else exercise.weightKg.toString()
        parts.add("${kg}kg")
    }
    return if (parts.isEmpty()) "" else "Normal: ${parts.joinToString(" - ")}"
}

private fun buildMetaText(exercise: Exercise): String {
    val hasTargetReps = exercise.targetReps.isNotBlank()
    val hasTargetWeight = exercise.targetWeightKg != null && exercise.targetWeightKg > 0
    if (!hasTargetReps && !hasTargetWeight) return ""

    val parts = mutableListOf<String>()
    if (hasTargetReps) parts.add(exercise.targetReps)
    if (hasTargetWeight) {
        val kg = if (exercise.targetWeightKg!! % 1.0 == 0.0)
            exercise.targetWeightKg.toInt().toString()
        else exercise.targetWeightKg.toString()
        parts.add("${kg}kg")
    }
    return "Meta: ${parts.joinToString(" - ")}"
}

private fun buildSeriesText(exercise: Exercise): String {
    return when {
        exercise.durationMinutes != null -> {
            "${exercise.sets} séries × ${exercise.reps}"
        }
        else -> {
            "${exercise.sets} séries × ${exercise.reps}"
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF080D1A)
@Composable
private fun TreinoDoDiaSectionPreview() {
    MyGymTheme {
        TreinoDoDiaSection(
            headerDateText = "Sexta-Feira, 12 De Setembro",
            workout = Workout(
                id = 1L,
                title = "Cardio & Core",
                tag = "CC",
                dayOfWeek = DayOfWeek.FRIDAY,
                exercises = listOf(
                    Exercise(
                        id = 1L,
                        name = "Corrida na Esteira",
                        sets = 1,
                        reps = "15 min",
                        targetReps = "20 min"
                    ),
                    Exercise(
                        id = 2L,
                        name = "Prancha Abdominal",
                        sets = 3,
                        reps = "45 seg",
                        targetReps = "60 seg",
                        isCompleted = true
                    ),
                    Exercise(
                        id = 3L,
                        name = "Russian Twist com Carga",
                        sets = 3,
                        reps = "20 reps",
                        weightKg = 6.0,
                        targetReps = "25 reps",
                        targetWeightKg = 8.0
                    )
                )
            ),
            onToggleExercise = {}
        )
    }
}

