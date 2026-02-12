package com.example.todolist.model.entity

enum class TaskState {
    TODO("À faire"),
    LATE("En retard"),
    DONE("Réalisée");

    private val displayName: String;

    constructor(displayName: String) {
        this.displayName = displayName
    }

    override fun toString(): String {
        return displayName
    }
}