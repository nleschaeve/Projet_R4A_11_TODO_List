package com.example.todolist.data.local

import androidx.room.*
import com.example.todolist.model.entity.Reward
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {

    @Insert
    suspend fun insert(reward: Reward)

    @Query("SELECT * FROM rewards ORDER BY timestamp DESC")
    fun getAllRewards(): Flow<List<Reward>>

    @Query("SELECT SUM(points) FROM rewards")
    fun getTotalPoints(): Flow<Int?>

    @Query("SELECT * FROM rewards ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentRewards(limit: Int = 10): Flow<List<Reward>>

    @Query("DELETE FROM rewards WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM rewards")
    suspend fun deleteAll()
}
