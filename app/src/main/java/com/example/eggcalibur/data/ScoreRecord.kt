package com.example.eggcalibur.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class ScoreRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val date: String
)