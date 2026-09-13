package com.mygym.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mygym.android.ui.navigation.MyGymApp
import com.mygym.android.ui.theme.MyGymTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as MyGymApplication).container

        setContent {
            MyGymTheme {
                MyGymApp(appContainer = appContainer)
            }
        }
    }
}
