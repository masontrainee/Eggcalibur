package com.example.eggcalibur.model

data class GameState(
    val playerX: Float = 0f,
    val playerY: Float = 0f,
    val playerVelocityX: Float = 0f,
    val playerVelocityY: Float = 0f,
    val isFacingRight: Boolean = true,
    val platforms: List<Platform> = emptyList(),
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
    val score: Int = 0
)

data class Platform(
    val id: Int,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)