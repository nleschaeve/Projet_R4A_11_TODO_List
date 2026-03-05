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
    }
}