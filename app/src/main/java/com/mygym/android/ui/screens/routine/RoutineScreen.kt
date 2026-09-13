package com.mygym.android.ui.screens.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mygym.android.domain.model.Workout
import com.mygym.android.ui.theme.PulseBackground
import com.mygym.android.ui.theme.PulseBlue
import com.mygym.android.ui.theme.PulseBorder
import com.mygym.android.ui.theme.PulseSurface
import com.mygym.android.ui.theme.PulseSurfaceVariant
import com.mygym.android.ui.theme.PulseTextMuted
import com.mygym.android.ui.theme.PulseTextPrimary
import com.mygym.android.ui.theme.PulseTextSecondary
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun RoutineScreen(
    viewModel: RoutineViewModel,
    onWorkoutClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val workouts by viewModel.workouts.collectAsState()
    val daysOfWeek = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PulseBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Rotina Semanal",
                color = PulseTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Planejamento dos seus treinos de Segunda a Domingo",
                color = PulseTextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(daysOfWeek, key = { it.name }) { day ->
            val workout = workouts.firstOrNull { it.dayOfWeek == day }
            RoutineDayItem(
                dayOfWeek = day,
                workout = workout,
                onWorkoutClick = {
                    workout?.let { onWorkoutClick(it.id) }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RoutineDayItem(
    dayOfWeek: DayOfWeek,
    workout: Workout?,
    onWorkoutClick: () -> Unit
) {
    val ptBr = Locale("pt", "BR")
    val dayName = dayOfWeek.getDisplayName(TextStyle.FULL, ptBr)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(ptBr) else it.toString() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = dayName,
            color = PulseTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        if (workout != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(onClick = onWorkoutClick),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PulseSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(PulseBorder),
                    width = 1.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(Color(workout.colorPrimaryHex), Color(workout.colorSecondaryHex))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = workout.tag,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = workout.title,
                            color = PulseTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${workout.exercises.size} exercícios • ${workout.estimatedMinutes} min",
                            color = PulseTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Ver treino",
                        tint = PulseTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PulseSurfaceVariant.copy(alpha = 0.5f)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(PulseBorder.copy(alpha = 0.5f)),
                    width = 1.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Descanso / Livre",
                        color = PulseTextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

