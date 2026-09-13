package com.mygym.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mygym.android.domain.model.DayProgress
import com.mygym.android.domain.model.WeekProgress
import com.mygym.android.domain.util.DateUtils
import com.mygym.android.ui.theme.GymSuccess
import com.mygym.android.ui.theme.MyGymTheme
import com.mygym.android.ui.theme.PulseBlue
import com.mygym.android.ui.theme.PulseBorder
import com.mygym.android.ui.theme.PulseDotInactive
import com.mygym.android.ui.theme.PulseSurface
import com.mygym.android.ui.theme.PulseSurfaceVariant
import com.mygym.android.ui.theme.PulseTextMuted
import com.mygym.android.ui.theme.PulseTextPrimary
import com.mygym.android.ui.theme.PulseTextSecondary
import java.time.LocalDate

@Composable
fun WeekCalendarCard(
    weekProgress: WeekProgress,
    selectedDate: LocalDate = DateUtils.getToday(),
    onDateSelected: (LocalDate) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isWeekViewSelected by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = PulseSurface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(PulseBorder),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Month Title & View Switch (Semana / Mês)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weekProgress.monthYearText,
                    color = PulseTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                // Segmented Toggle Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(PulseSurfaceVariant)
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "Semana" Option
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isWeekViewSelected) PulseBlue else Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { isWeekViewSelected = true }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Semana",
                            color = if (isWeekViewSelected) Color.White else PulseTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isWeekViewSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }

                    // "Mês" Option
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (!isWeekViewSelected) PulseBlue else Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { isWeekViewSelected = false }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mês",
                            color = if (!isWeekViewSelected) Color.White else PulseTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (!isWeekViewSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Calendar Days Row (Sunday to Saturday)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekProgress.days.forEach { day ->
                    val isSelected = day.date == selectedDate

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onDateSelected(day.date) }
                    ) {
                        // Day of week letter (D, S, T, Q, Q, S, S)
                        Text(
                            text = day.dayOfWeekLetter,
                            color = if (isSelected) PulseTextPrimary else PulseTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Day number with circular highlight when selected
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) PulseBlue else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.dayNumber.toString(),
                                color = if (isSelected) Color.White else PulseTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Activity / Streak Indicator Dot
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        day.hasCompletedWorkout -> GymSuccess
                                        day.hasScheduledWorkout -> PulseBlue.copy(alpha = 0.7f)
                                        isSelected -> PulseDotInactive
                                        else -> PulseDotInactive.copy(alpha = 0.4f)
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Bottom Quick Stats Row: "Sequência ativa" & "Meta semanal"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Active Streak
                Column {
                    Text(
                        text = "Sequência ativa",
                        color = PulseTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${weekProgress.activeStreakDays} dias",
                        color = PulseBlue,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Weekly Goal
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Meta semanal",
                        color = PulseTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${weekProgress.weeklyGoalCompleted}/${weekProgress.weeklyGoalTotal}",
                        color = PulseTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080D1A)
@Composable
fun WeekCalendarCardPreview() {
    val sampleProgress = DateUtils.buildWeekProgress(
        completedDates = setOf(DateUtils.getToday())
    )
    MyGymTheme {
        WeekCalendarCard(
            weekProgress = sampleProgress,
            selectedDate = DateUtils.getToday()
        )
    }
}
