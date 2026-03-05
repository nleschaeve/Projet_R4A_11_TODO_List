package com.example.todolist.model.repository

import com.example.todolist.data.local.TaskDao
import com.example.todolist.model.entity.Task
import com.example.todolist.model.entity.TaskState
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val dataSource: TaskDao
) : TaskRepository {

    override suspend fun add(task: Task) {
        dataSource.insert(task)
    }

    override fun getAll(): Flow<List<Task>> {
        return dataSource.getAll()
    }

    override fun getByState(state: TaskState): Flow<List<Task>> {
        return dataSource.getByState(state)
    }
}