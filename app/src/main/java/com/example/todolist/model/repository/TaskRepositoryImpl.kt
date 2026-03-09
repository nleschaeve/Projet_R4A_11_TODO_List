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

    override suspend fun remove(task: Task) {
        dataSource.delete(task)
    }

    override suspend fun update(task: Task) {
        dataSource.update(task)
    }

    override fun getAll(): Flow<List<Task>> {
        return dataSource.getAll()
    }

    override fun getById(id: Int): Flow<Task?> {
        return dataSource.getById(id)
    }

    override fun getByState(state: TaskState): Flow<List<Task>> {
        return dataSource.getByState(state)
    }
}