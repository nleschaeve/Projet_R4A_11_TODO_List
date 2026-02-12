package com.example.todolist.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: LocalDate?,
    val dueTime: LocalTime?,
    val state: TaskState,
    val imageUri: String? = null
)