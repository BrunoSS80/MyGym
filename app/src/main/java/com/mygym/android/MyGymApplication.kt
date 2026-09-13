package com.mygym.android

import android.app.Application
import com.mygym.android.di.AppContainer
import com.mygym.android.di.DefaultAppContainer

class MyGymApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
