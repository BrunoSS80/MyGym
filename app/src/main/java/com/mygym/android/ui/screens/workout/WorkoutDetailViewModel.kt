package com.mygym.android.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mygym.android.data.repository.WorkoutRepository
import com.mygym.android.domain.model.Workout
import com.mygym.android.domain.util.DateUtils
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface WorkoutUiEvent {
    data object WorkoutCompletedSuccessfully : WorkoutUiEvent
}

class WorkoutDetailViewModel(
    private val workoutId: Long,
    private val date: LocalDate = DateUtils.getToday(),
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<WorkoutUiEvent>()
    val events: SharedFlow<WorkoutUiEvent> = _events.asSharedFlow()

    val workout: StateFlow<Workout?> = repository.getWorkoutById(workoutId, date)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun toggleExercise(exerciseId: Long) {
        viewModelScope.launch {
            repository.toggleExerciseCompletion(workoutId, exerciseId, date)
        }
    }

    fun completeWorkout() {
        viewModelScope.launch {
            repository.completeWorkout(workoutId, date)
            _events.emit(WorkoutUiEvent.WorkoutCompletedSuccessfully)
        }
    }

    fun uncompleteWorkout() {
        viewModelScope.launch {
            repository.uncompleteWorkout(workoutId, date)
        }
    }

    companion object {
        fun provideFactory(
            workoutId: Long,
            date: LocalDate = DateUtils.getToday(),
            repository: WorkoutRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WorkoutDetailViewModel(workoutId, date, repository) as T
            }
        }
    }
}

