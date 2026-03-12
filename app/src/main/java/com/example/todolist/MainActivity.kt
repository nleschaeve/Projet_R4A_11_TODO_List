package com.example.todolist.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.todolist.controller.TaskController
import com.example.todolist.controller.TaskManager
import com.example.todolist.data.local.AppDatabase
import com.example.todolist.model.repository.TaskRepositoryImpl
import com.example.todolist.model.service.TaskStatusService
import com.example.todolist.model.service.RewardService
import com.example.todolist.navigation.AppNavGraph
import com.example.todolist.ui.theme.TicTaskTheme
import kotlinx.coroutines.launch

class TaskListActivity : ComponentActivity() {

    private lateinit var database: AppDatabase

    private lateinit var taskManager: TaskManager
    private lateinit var taskController: TaskController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de la base de données
        database = AppDatabase.getInstance(this)

        val dao = database.taskDao()
        val rewardDao = database.rewardDao()
        val repository = TaskRepositoryImpl(dao)
        val service = TaskStatusService()
        val rewardService = RewardService(rewardDao)

        taskManager = TaskManager(repository, service)

        // Initialisation du TaskController
        taskController = TaskController(repository, service, rewardService)

        setContent {
            TicTaskTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    taskController = taskController,
                    taskDao = dao
                )
            }
        }

        lifecycleScope.launch {
            taskController.checkAndUpdateLateTasks()
        }
    }
}