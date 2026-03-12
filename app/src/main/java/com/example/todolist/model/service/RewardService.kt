package com.example.todolist.model.service

import com.example.todolist.data.local.RewardDao
import com.example.todolist.model.entity.Reward
import com.example.todolist.model.entity.Task
import com.example.todolist.model.entity.TaskPriority
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class RewardService(private val rewardDao: RewardDao) {

    /**
     * Calcule les points de récompense en fonction de la priorité de la tâche
     */
    fun calculatePoints(task: Task): Int {
        return when (task.priority) {
            TaskPriority.CRITICAL -> 50  // Tâche primordiale
            TaskPriority.IMPORTANT -> 30 // Tâche importante
            TaskPriority.NONE -> 10      // Tâche normale
        }
    }

    /**
     * Attribue une récompense pour une tâche complétée
     */
    suspend fun rewardTaskCompletion(task: Task) {
        val points = calculatePoints(task)
        val reward = Reward(
            taskTitle = task.title,
            points = points,
            timestamp = LocalDateTime.now(),
            taskPriority = task.priority
        )
        rewardDao.insert(reward)
    }

    /**
     * Récupère toutes les récompenses
     */
    fun getAllRewards(): Flow<List<Reward>> {
        return rewardDao.getAllRewards()
    }

    /**
     * Récupère le total de points
     */
    fun getTotalPoints(): Flow<Int?> {
        return rewardDao.getTotalPoints()
    }

    /**
     * Récupère les récompenses récentes
     */
    fun getRecentRewards(limit: Int = 10): Flow<List<Reward>> {
        return rewardDao.getRecentRewards(limit)
    }
}
