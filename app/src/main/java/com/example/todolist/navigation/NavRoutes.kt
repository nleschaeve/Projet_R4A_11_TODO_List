package com.example.todolist.navigation


// Définition des routes de navigation de l'application.
sealed class NavRoutes(val route: String) {
    object TaskList : NavRoutes("task_list")
    object TaskAdd : NavRoutes("task_add")
}