package com.ozan.kotlinaiwork.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ozan.kotlinaiwork.screens.ProjectListScreen
import com.ozan.kotlinaiwork.screens.ProjectDetail
import com.ozan.kotlinaiwork.screens.project.ProjectEditScreen
import com.ozan.kotlinaiwork.viewmodel.SharedViewModel


sealed class Screen(val route: String) {
    object ProjectList : Screen("project_list")
    object ProjectDetail : Screen("project_detail")
    object AddProject : Screen("add_project")
    object EditProject : Screen("edit_project/{projectId}") {
        fun createRoute(projectId: String) = "edit_project/$projectId"
    }

    companion object {
        const val PROJECT_ID_ARG = "projectId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.ProjectList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.ProjectList.route) {
            val sharedViewModel: SharedViewModel = hiltViewModel()
            ProjectListScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                sharedViewModel = sharedViewModel
            )
        }

        composable(Screen.AddProject.route) {
            val viewModel: SharedViewModel = hiltViewModel()
            ProjectEditScreen(
                sharedViewModel = viewModel,
                onBack = { navController.navigateUp() },
                onSave = { navController.navigateUp() },
                navController = navController
            )
        }

        composable(Screen.ProjectDetail.route) {
            ProjectDetail(onBack = { navController.navigateUp() },onSave = { navController.navigateUp() },
                navController = navController)
        }




        composable(
            route = Screen.EditProject.route,
            arguments = listOf(
                navArgument(Screen.PROJECT_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) {
            val viewModel: SharedViewModel = hiltViewModel()
            ProjectEditScreen(
                sharedViewModel = viewModel,
                onBack = { navController.navigateUp() },
                onSave = { navController.navigateUp() },
                navController = navController
            )
        }
    }
}

