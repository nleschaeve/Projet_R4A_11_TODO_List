package com.example.todolist.data.local

import androidx.room.*
import com.example.todolist.model.entity.Task
import com.example.todolist.model.entity.TaskState
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Query("SELECT * FROM tasks WHERE state = :state")
    fun getByState(state: TaskState): Flow<List<Task>>

    @Query("SELECT * FROM tasks")
    fun getAll(): Flow<List<Task>>
}