package com.example.todolist.model.service

import com.example.todolist.model.entity.Task
import com.example.todolist.model.entity.TaskState
import java.time.LocalDateTime

class TaskStatusService {

    fun isLate(task: Task): Boolean {
        val date = task.dueDate ?: return false
        val time = task.dueTime ?: return false

        return LocalDateTime.now()
            .isAfter(LocalDateTime.of(date, time))
    }

    fun markAsTodo(task: Task): Task =
        task.copy(state = TaskState.TODO)

    fun markAsLate(task: Task): Task =
        task.copy(state = TaskState.LATE)

    fun markAsDone(task: Task): Task =
        task.copy(state = TaskState.DONE)
}