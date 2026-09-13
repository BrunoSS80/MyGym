package com.mygym.android.di

import android.content.Context
import com.mygym.android.data.local.MyGymDatabase
import com.mygym.android.data.repository.WorkoutRepository
import com.mygym.android.data.repository.WorkoutRepositoryImpl

interface AppContainer {
    val workoutRepository: WorkoutRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: MyGymDatabase by lazy {
        MyGymDatabase.getDatabase(context)
    }

    override val workoutRepository: WorkoutRepository by lazy {
        WorkoutRepositoryImpl(
            workoutDao = database.workoutDao(),
            workoutLogDao = database.workoutLogDao()
        )
    }
}

