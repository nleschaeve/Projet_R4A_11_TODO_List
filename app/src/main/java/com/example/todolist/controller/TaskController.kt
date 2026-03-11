package com.example.todolist.controller

import com.example.todolist.model.entity.Task
import com.example.todolist.model.entity.TaskState
import com.example.todolist.model.repository.TaskRepository
import com.example.todolist.model.service.TaskStatusService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TaskController(
    private val repository: TaskRepository,
    private val statusService: TaskStatusService
) {

    suspend fun addTask(task: Task) {
        repository.add(task)
    }

    suspend fun removeTask(task: Task) {
        repository.remove(task)
    }

    fun getTaskById(id: Int): Flow<Task?> {
        return repository.getById(id)
    }

    fun getAllTasks(): Flow<List<Task>> {
        return repository.getAll()
    }

    fun getTasksByState(state: TaskState): Flow<List<Task>> {
        return repository.getByState(state)
    }

    suspend fun updateTask(task: Task) {
        repository.update(task)
    }

    suspend fun markTaskAsTodo(task: Task) {
        val updated = statusService.markAsTodo(task)
        repository.update(updated)
    }

    suspend fun completeTask(task: Task) {
        val updated = statusService.markAsDone(task)
        repository.update(updated)

        // Si la tâche est périodique, créer la prochaine occurrence
        val nextOccurrence = statusService.getNextOccurrence(task)
        if (nextOccurrence != null) {
            repository.add(nextOccurrence)
        }
    }

    suspend fun checkAndUpdateLateTasks(): Int {
        val todoTasks = repository.getByState(TaskState.TODO).first()
        var updatedCount = 0
        todoTasks.forEach { task ->
            if (statusService.isLate(task)) {
                repository.update(statusService.markAsLate(task))
                updatedCount++
            }
        }
        return updatedCount
    }
}