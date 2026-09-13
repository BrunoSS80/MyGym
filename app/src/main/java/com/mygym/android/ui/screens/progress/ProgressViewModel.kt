package com.mygym.android.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mygym.android.data.repository.WorkoutRepository
import com.mygym.android.domain.model.WeekProgress
import com.mygym.android.domain.model.WorkoutLog
import com.mygym.android.domain.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    val weekProgress: StateFlow<WeekProgress> = repository.getWeeklyProgress()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DateUtils.buildWeekProgress(completedDates = emptySet())
        )

    val workoutLogs: StateFlow<List<WorkoutLog>> = repository.getAllWorkoutLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    companion object {
        fun provideFactory(repository: WorkoutRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProgressViewModel(repository) as T
                }
            }
    }
}

