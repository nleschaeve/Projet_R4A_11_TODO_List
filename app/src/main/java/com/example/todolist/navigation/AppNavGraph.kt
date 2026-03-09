package com.example.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todolist.controller.TaskController
import com.example.todolist.data.local.TaskDao
import com.example.todolist.view.TaskAdd
import com.example.todolist.view.TaskListView
import com.example.todolist.view.TaskDetail
import com.example.todolist.view.TaskModify

@Composable
fun AppNavGraph(
    navController: NavHostController,
    taskController: TaskController,
    taskDao: TaskDao
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.TaskList.route
    ) {
        // Écran principal - Liste des tâches
        composable(NavRoutes.TaskList.route) {
            TaskListView(
                controller = taskController,
                navController = navController
            )
        }

        // Écran d'ajout de tâche
        composable(NavRoutes.TaskAdd.route) {
            TaskAdd(
                controller = taskController,
                navController = navController
            )
        }

        // Écran de détails d'une tâche
        composable(
            route = NavRoutes.TaskDetail.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            TaskDetail(
                controller = taskController,
                navController = navController,
                taskId = taskId
            )
        }

        // Écran de modification de tâche
        composable(
            route = NavRoutes.TaskModify.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            TaskModify(
                controller = taskController,
                navController = navController,
                taskId = taskId
            )
        }
    }
}