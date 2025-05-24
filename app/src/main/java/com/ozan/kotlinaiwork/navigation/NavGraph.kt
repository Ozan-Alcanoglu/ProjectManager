package com.ozan.kotlinaiwork.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ozan.kotlinaiwork.screens.ProjectDetail
import com.ozan.kotlinaiwork.screens.ProjectListScreen
import com.ozan.kotlinaiwork.screens.project.ProjectEditScreen
import com.ozan.kotlinaiwork.viewmodel.ProjectEditViewModel
import com.ozan.kotlinaiwork.viewmodel.ProjectEvent
import com.ozan.kotlinaiwork.viewmodel.ProjectViewModel

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
            val viewModel: ProjectViewModel = hiltViewModel()
            ProjectListScreen(
                onNavigate = { event ->
                    when (event) {
                        is ProjectEvent.NavigateToAddProject -> {
                            navController.navigate(Screen.AddProject.route)
                        }
                        is ProjectEvent.NavigateToProjectDetail -> {
                            navController.navigate(
                                Screen.EditProject.createRoute(event.projectId)
                            )
                        }
                        else -> { /* Diğer event'ler */ }
                    }
                },
                viewModel = viewModel
            )
        }

        composable(Screen.AddProject.route) {
            val viewModel: ProjectEditViewModel = hiltViewModel()
            ProjectEditScreen(
                viewModel = viewModel,
                projectId = null,
                onBack = { navController.navigateUp() },
                onSave = { navController.navigateUp() },
                navController = navController
            )
        }

        composable(Screen.ProjectDetail.route) {
            ProjectDetail()
        }




        composable(
            route = Screen.EditProject.route,
            arguments = listOf(
                navArgument(Screen.PROJECT_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString(Screen.PROJECT_ID_ARG) ?: ""
            val viewModel: ProjectEditViewModel = hiltViewModel()
            ProjectEditScreen(
                viewModel = viewModel,
                projectId = projectId,
                onBack = { navController.navigateUp() },
                onSave = { navController.navigateUp() },
                navController = navController
            )
        }
    }
}

