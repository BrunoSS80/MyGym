package com.mygym.android.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mygym.android.di.AppContainer
import com.mygym.android.ui.components.PulseBottomNavigation
import com.mygym.android.ui.components.PulseNavTab
import com.mygym.android.ui.screens.HomeScreen
import com.mygym.android.ui.screens.account.AccountScreen
import com.mygym.android.ui.screens.home.HomeViewModel
import com.mygym.android.ui.screens.progress.ProgressScreen
import com.mygym.android.ui.screens.progress.ProgressViewModel
import com.mygym.android.ui.screens.routine.RoutineScreen
import com.mygym.android.ui.screens.routine.RoutineViewModel
import com.mygym.android.ui.screens.workout.WorkoutDetailScreen
import com.mygym.android.ui.screens.workout.WorkoutDetailViewModel
import com.mygym.android.ui.theme.PulseBackground

object Routes {
    const val HOME = "home"
    const val ROUTINE = "routine"
    const val PROGRESS = "progress"
    const val ACCOUNT = "account"
    const val WORKOUT_DETAIL = "workout_detail/{workoutId}"

    fun workoutDetail(workoutId: Long): String = "workout_detail/$workoutId"
}

@Composable
fun MyGymApp(
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentTab = when (currentRoute) {
        Routes.HOME -> PulseNavTab.INICIO
        Routes.ROUTINE -> PulseNavTab.TREINOS
        Routes.PROGRESS -> PulseNavTab.PROGRESSO
        Routes.ACCOUNT -> PulseNavTab.CONTA
        else -> null
    }

    val showBottomBar = currentTab != null

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PulseBackground,
        bottomBar = {
            if (currentTab != null) {
                PulseBottomNavigation(
                    selectedTab = currentTab,
                    onTabSelected = { tab ->
                        val targetRoute = when (tab) {
                            PulseNavTab.INICIO -> Routes.HOME
                            PulseNavTab.TREINOS -> Routes.ROUTINE
                            PulseNavTab.PROGRESSO -> Routes.PROGRESS
                            PulseNavTab.CONTA -> Routes.ACCOUNT
                        }
                        if (targetRoute != currentRoute) {
                            navController.navigate(targetRoute) {
                                popUpTo(Routes.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp(0f))
        ) {
            composable(Routes.HOME) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.provideFactory(appContainer.workoutRepository)
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    onWorkoutClick = { workoutId ->
                        navController.navigate(Routes.workoutDetail(workoutId))
                    }
                )
            }

            composable(Routes.ROUTINE) {
                val routineViewModel: RoutineViewModel = viewModel(
                    factory = RoutineViewModel.provideFactory(appContainer.workoutRepository)
                )
                RoutineScreen(
                    viewModel = routineViewModel,
                    onWorkoutClick = { workoutId ->
                        navController.navigate(Routes.workoutDetail(workoutId))
                    }
                )
            }

            composable(Routes.PROGRESS) {
                val progressViewModel: ProgressViewModel = viewModel(
                    factory = ProgressViewModel.provideFactory(appContainer.workoutRepository)
                )
                ProgressScreen(viewModel = progressViewModel)
            }

            composable(Routes.ACCOUNT) {
                AccountScreen()
            }

            composable(
                route = Routes.WORKOUT_DETAIL,
                arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
                val workoutViewModel: WorkoutDetailViewModel = viewModel(
                    factory = WorkoutDetailViewModel.provideFactory(
                        workoutId = workoutId,
                        repository = appContainer.workoutRepository
                    )
                )
                WorkoutDetailScreen(
                    viewModel = workoutViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

