package com.mygym.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mygym.android.domain.util.DateUtils
import com.mygym.android.ui.components.PulseHeader
import com.mygym.android.ui.components.RestDayCard
import com.mygym.android.ui.components.TreinoDoDiaSection
import com.mygym.android.ui.components.WeekCalendarCard
import com.mygym.android.ui.screens.home.HomeViewModel
import com.mygym.android.ui.theme.MyGymTheme
import com.mygym.android.ui.theme.PulseBackground
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onWorkoutClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val weekProgress by viewModel.weekProgress.collectAsState()
    val currentWorkout by viewModel.currentWorkout.collectAsState()
    val scrollState = rememberScrollState()

    val headerDateText = DateUtils.formatHeaderDate(selectedDate)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PulseBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // 1. Header Section
        PulseHeader(
            userName = "Rafael",
            userLevel = 7
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Weekly Calendar & Streak Card
        WeekCalendarCard(
            weekProgress = weekProgress,
            selectedDate = selectedDate,
            onDateSelected = { viewModel.onDateSelected(it) }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 3. Treino do dia — novo estilo expandido com lista de exercícios
        if (currentWorkout != null) {
            TreinoDoDiaSection(
                headerDateText = headerDateText,
                workout = currentWorkout!!,
                onToggleExercise = { exerciseId -> viewModel.toggleExercise(exerciseId) }
            )
        } else {
            // Sem treino para o dia selecionado
            RestDayCard()
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
