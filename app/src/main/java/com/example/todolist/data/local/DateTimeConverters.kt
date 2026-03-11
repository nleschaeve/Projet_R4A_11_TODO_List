package com.example.todolist.data.local

import androidx.room.TypeConverter
import com.example.todolist.model.entity.TaskState
import com.example.todolist.model.entity.TaskPeriodicity
import com.example.todolist.model.entity.TaskPriority
import java.time.LocalDate
import java.time.LocalTime

class DateTimeConverters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromTaskState(state: TaskState): String {
        return state.name
    }

    @TypeConverter
    fun toTaskState(stateString: String): TaskState {
        return TaskState.valueOf(stateString)
    }

    @TypeConverter
    fun fromTaskPeriodicity(periodicity: TaskPeriodicity): String {
        return periodicity.name
    }

    @TypeConverter
    fun toTaskPeriodicity(periodicityString: String): TaskPeriodicity {
        return TaskPeriodicity.valueOf(periodicityString)
    }

    @TypeConverter
    fun fromTaskPriority(priority: TaskPriority): String {
        return priority.name
    }

    @TypeConverter
    fun toTaskPriority(priorityString: String): TaskPriority {
        return TaskPriority.valueOf(priorityString)
    }
}