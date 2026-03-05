package com.example.todolist.data.local

import androidx.room.TypeConverter
import com.example.todolist.model.entity.TaskState
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
}