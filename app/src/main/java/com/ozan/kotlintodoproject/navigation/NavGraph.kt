package com.ozan.kotlintodoproject.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ozan.kotlintodoproject.screens.ProjectListScreen
import com.ozan.kotlintodoproject.screens.ProjectDetail
import com.ozan.kotlintodoproject.screens.ProjectCreateScreen
import com.ozan.kotlintodoproject.screens.UpdateProject
import com.ozan.kotlintodoproject.screens.UpdateProjectDetailScreen
import com.ozan.kotlintodoproject.viewmodel.ProjectViewModel
import com.ozan.kotlintodoproject.viewmodel.SharedViewModel


sealed class Screen(val route: String) {
    object ProjectList : Screen("project_list")
    object ProjectDetail : Screen("project_detail")
    object UpdateProject : Screen("update_project")
    object UpdateProjectDetailScreen : Screen("update_projectdetail/{projectId}") {
        fun createRoute(projectId: String) = "update_projectdetail/$projectId"
    }
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

    val sharedViewModel: SharedViewModel = hiltViewModel()
    val projectViewModel: ProjectViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.ProjectList.route) {
            ProjectListScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                projectViewModel = projectViewModel
            )
        }

        composable(Screen.AddProject.route) {
            ProjectCreateScreen(
                sharedViewModel=sharedViewModel,
                onBack = { navController.navigateUp() },
                onSave = { navController.navigateUp() },
                navController = navController
            )
        }

        composable(Screen.ProjectDetail.route) {
            ProjectDetail(
                sharedViewModel=sharedViewModel,
                onBack = { navController.navigateUp() },
                onSave = {
                    navController.navigate(Screen.ProjectList.route) {
                        popUpTo(Screen.ProjectList.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
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
            ProjectCreateScreen(
                sharedViewModel=sharedViewModel,
                onBack = { navController.navigateUp() },
                onSave = { navController.navigateUp() },
                navController = navController
            )
        }

        composable(Screen.UpdateProject.route) {
            UpdateProject(
                onBack = {navController.navigateUp()},
                projectViewModel = projectViewModel,
                navController = navController
            )
        }

        composable(
            route = Screen.UpdateProjectDetailScreen.route,
            arguments = listOf(
                navArgument("projectId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            UpdateProjectDetailScreen(
                projectId = projectId,
                onBack = { navController.navigateUp() },
                projectViewModel = projectViewModel,
                sharedViewModel = sharedViewModel
            )
        }

    }
}

