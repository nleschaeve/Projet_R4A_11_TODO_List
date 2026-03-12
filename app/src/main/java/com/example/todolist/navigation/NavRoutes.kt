package com.example.todolist.navigation


// Définition des routes de navigation de l'application.
sealed class NavRoutes(val route: String) {
    object TaskList : NavRoutes("task_list")
    object TaskAdd : NavRoutes("task_add")
    object Rewards : NavRoutes("rewards")
    object TaskDetail : NavRoutes("task_detail/{taskId}") {
        fun createRoute(taskId: Int) = "task_detail/$taskId"
    }
    object TaskModify : NavRoutes("task_modify/{taskId}") {
        fun createRoute(taskId: Int) = "task_modify/$taskId"
    }
}