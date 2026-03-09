package com.example.todolist.controller

import com.example.todolist.model.entity.Task
import com.example.todolist.model.entity.TaskState
import com.example.todolist.model.repository.TaskRepository
import com.example.todolist.model.service.TaskStatusService
import kotlinx.coroutines.flow.Flow

class TaskManager(
    private val repository: TaskRepository,
    private val statusService: TaskStatusService
) {

    suspend fun addTask(task: Task) {
        repository.add(task)
    }

    suspend fun removeTask(task: Task) {
        repository.remove(task)
    }

    fun getTasksByState(state: TaskState): Flow<List<Task>> {
        return repository.getByState(state)
    }
}