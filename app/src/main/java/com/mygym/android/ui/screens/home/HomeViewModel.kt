package com.mygym.android.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mygym.android.data.repository.WorkoutRepository
import com.mygym.android.domain.model.WeekProgress
import com.mygym.android.domain.model.Workout
import com.mygym.android.domain.util.DateUtils
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Rafael",
    val userLevel: Int = 7,
    val selectedDate: LocalDate = DateUtils.getToday(),
    val headerDateText: String = DateUtils.formatHeaderDate(DateUtils.getToday()),
    val weekProgress: WeekProgress = DateUtils.buildWeekProgress(completedDates = emptySet()),
    val workoutForSelectedDate: Workout? = null,
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(DateUtils.getToday())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val weekProgress: StateFlow<WeekProgress> = repository.getWeeklyProgress()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DateUtils.buildWeekProgress(completedDates = emptySet())
        )

    val currentWorkout: StateFlow<Workout?> = _selectedDate.flatMapLatest { date ->
        repository.getWorkoutForDay(date.dayOfWeek, date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun toggleExercise(exerciseId: Long) {
        val workout = currentWorkout.value ?: return
        val date = _selectedDate.value
        viewModelScope.launch {
            repository.toggleExerciseCompletion(workout.id, exerciseId, date)
        }
    }

    companion object {
        fun provideFactory(repository: WorkoutRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
    }
}


