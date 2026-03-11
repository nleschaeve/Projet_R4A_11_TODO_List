package com.example.todolist.model.entity

enum class TaskPeriodicity {
    NONE("Aucune"),
    DAILY("Quotidienne"),
    WEEKLY("Hebdomadaire"),
    MONTHLY("Mensuelle");

    private val displayName: String

    constructor(displayName: String) {
        this.displayName = displayName
    }

    override fun toString(): String {
        return displayName
    }
}
