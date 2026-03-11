package com.example.todolist.model.entity

enum class TaskPriority {
    NONE("Aucune"),
    IMPORTANT("Importante"),
    CRITICAL("Primordiale");

    private val displayName: String

    constructor(displayName: String) {
        this.displayName = displayName
    }

    override fun toString(): String {
        return displayName
    }
}
