package com.example.eggcalibur.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    // Зберегти результат
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ScoreRecord)

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 10")
    fun getTopScores(): Flow<List<ScoreRecord>>
}