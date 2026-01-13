package com.example.eggcalibur.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eggcalibur.model.GameState
import com.example.eggcalibur.model.Platform
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    // Константи
    private val GRAVITY = 0.8f
    private val JUMP_STRENGTH = -25f
    private val MOVEMENT_SPEED = 25f
    private val SCREEN_WIDTH = 1080f
    private val SCREEN_HEIGHT = 1920f
    private val PLATFORM_SHORT = 100f
    private val PLATFORM_LONG = 200f

    init {
        generateInitialPlatforms()
        startGameLoop()
    }

    private fun generateInitialPlatforms() {
        val platforms = mutableListOf<Platform>()
        platforms.add(Platform(id = 0, x = 400f, y = 1200f, width = PLATFORM_LONG))

        for (i in 1..10) {
            val width = if (Random.nextBoolean()) PLATFORM_LONG else PLATFORM_SHORT
            platforms.add(
                Platform(
                    id = i,
                    x = Random.nextFloat() * (SCREEN_WIDTH - width),
                    y = 1200f - (i * 300f),
                    width = width
                )
            )
        }
        _gameState.update { it.copy(platforms = platforms) }
    }

    fun onJoystickMoved(percentX: Float, percentY: Float) {
        _gameState.update {
            it.copy(playerVelocityX = percentX * MOVEMENT_SPEED)
        }
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            while (true) {
                updatePhysics()
                delay(16L)
            }
        }
    }

    private fun updatePhysics() {
        _gameState.update { state ->
            if (state.isGameOver) return@update state

            // 1. Рух по X
            var newX = state.playerX + state.playerVelocityX

            // Визначаємо напрямок погляду
            var newFacingRight = state.isFacingRight
            if (state.playerVelocityX > 0.1f) {
                newFacingRight = true
            } else if (state.playerVelocityX < -0.1f) {
                newFacingRight = false
            }

            // Телепорт (Wrapping)
            if (newX > SCREEN_WIDTH) {
                newX = 0f - 50f
            } else if (newX < -50f) {
                newX = SCREEN_WIDTH
            }

            // 2. Рух по Y
            var newVelocityY = state.playerVelocityY + GRAVITY
            var newY = state.playerY + newVelocityY
            var newPlatforms = state.platforms
            var newScore = state.score

            // Логіка камери
            if (newY < 800f) {
                val diff = 800f - newY
                newY = 800f

                newPlatforms = state.platforms.map {
                    it.copy(y = it.y + diff)
                }.filter {
                    it.y < SCREEN_HEIGHT + 100
                }.toMutableList()

                // Генерація нових платформ
                if (newPlatforms.isNotEmpty() && newPlatforms.last().y > 300f) {
                    val lastId = newPlatforms.maxOf { it.id }
                    val width = if (Random.nextBoolean()) PLATFORM_LONG else PLATFORM_SHORT
                    newPlatforms.add(
                        Platform(
                            id = lastId + 1,
                            x = Random.nextFloat() * (SCREEN_WIDTH - width),
                            y = newPlatforms.minOf { it.y } - 300f,
                            width = width
                        )
                    )
                }
                newScore += (diff / 10).toInt()
            }

            // Колізія
            if (newVelocityY > 0) {
                state.platforms.forEach { platform ->
                    val playerBottom = newY + 50f
                    val playerRight = newX + 50f
                    val playerLeft = newX - 50f

                    if (playerBottom >= platform.y &&
                        playerBottom <= platform.y + platform.height + 20f &&
                        playerRight > platform.x &&
                        playerLeft < platform.x + platform.width
                    ) {
                        newVelocityY = JUMP_STRENGTH
                    }
                }
            }

            val isGameOver = newY > SCREEN_HEIGHT + 200

            // Повертаємо оновлений стан
            state.copy(
                playerX = newX,
                playerY = newY,
                playerVelocityY = newVelocityY,
                platforms = newPlatforms,
                score = newScore,
                isGameOver = isGameOver,
                isFacingRight = newFacingRight // Тепер ця змінна точно доступна
            )
        }
    }
}