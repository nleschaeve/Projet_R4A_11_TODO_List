package com.example.todolist.controller

import com.example.todolist.model.entity.Task
import com.example.todolist.model.entity.TaskState
import com.example.todolist.model.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class TaskController(
    private val repository: TaskRepository
) {

    suspend fun addTask(task: Task) {
        repository.add(task)
    }

    fun getAllTasks(): Flow<List<Task>> {
        return repository.getAll()
    }

    fun getTasksByState(state: TaskState): Flow<List<Task>> {
        return repository.getByState(state)
    }
}