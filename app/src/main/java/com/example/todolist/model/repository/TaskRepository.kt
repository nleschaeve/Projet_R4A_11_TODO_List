package com.example.todolist.model.repository

import com.example.todolist.model.entity.Task
import com.example.todolist.model.entity.TaskState
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun add(task: Task)

    suspend fun remove(task: Task)

    suspend fun update(task: Task)

    fun getAll(): Flow<List<Task>>

    fun getById(id: Int): Flow<Task?>

    fun getByState(state: TaskState): Flow<List<Task>>
}