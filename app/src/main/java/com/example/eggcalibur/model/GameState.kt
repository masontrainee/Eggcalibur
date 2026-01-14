package com.example.eggcalibur.model

data class GameState(
    val playerX: Float = 500f,
    val playerY: Float = 1000f,
    val playerVelocityY: Float = 0f,
    val playerVelocityX: Float = 0f,
    val isFacingRight: Boolean = true,
    val isGameOver: Boolean = false,
    val score: Int = 0,
    val platforms: List<Platform> = emptyList(),
    val isPaused: Boolean = false
)

data class Platform(
    val id: Int,
    val x: Float,
    val y: Float,
    val width: Float = 200f, // Це значення ми будемо змінювати
    val height: Float = 70f
)