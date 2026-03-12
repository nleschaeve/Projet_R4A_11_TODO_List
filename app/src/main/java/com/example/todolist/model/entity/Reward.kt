package com.example.todolist.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "rewards")
data class Reward(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskTitle: String,
    val points: Int,
    val timestamp: LocalDateTime,
    val taskPriority: TaskPriority
)
