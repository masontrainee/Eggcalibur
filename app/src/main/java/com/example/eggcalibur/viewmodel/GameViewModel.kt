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

    // Константи фізики
    private val GRAVITY = 0.8f
    private val JUMP_STRENGTH = -25f
    private val MOVEMENT_SPEED = 25f
    private val SCREEN_WIDTH = 1080f
    private val SCREEN_HEIGHT = 1920f

    // Розміри платформ
    private val PLATFORM_SHORT = 100f
    private val PLATFORM_LONG = 200f

    init {
        generateInitialPlatforms()
        startGameLoop()
    }

    // --- ЛОГІКА ГРИ ---

    private fun startGameLoop() {
        viewModelScope.launch {
            while (true) {
                // Оновлюємо гру, тільки якщо не пауза і не кінець
                if (!_gameState.value.isPaused && !_gameState.value.isGameOver) {
                    updatePhysics()
                }
                delay(16L) // ~60 FPS
            }
        }
    }

    private fun updatePhysics() {
        _gameState.update { state ->
            // 1. Рух по X
            var newX = state.playerX + state.playerVelocityX

            // Напрямок погляду
            var newFacingRight = state.isFacingRight
            if (state.playerVelocityX > 0.1f) newFacingRight = true
            else if (state.playerVelocityX < -0.1f) newFacingRight = false

            // Телепорт по краях екрану
            if (newX > SCREEN_WIDTH) newX = -50f
            else if (newX < -50f) newX = SCREEN_WIDTH

            // 2. Рух по Y (Гравітація)
            var newVelocityY = state.playerVelocityY + GRAVITY
            var newY = state.playerY + newVelocityY

            var newPlatforms = state.platforms
            var newScore = state.score

            // 3. Камера (Рух платформ вниз)
            if (newY < 800f) {
                val diff = 800f - newY
                newY = 800f

                newPlatforms = state.platforms.map {
                    it.copy(y = it.y + diff)
                }.filter {
                    it.y < SCREEN_HEIGHT + 100 // Видаляємо ті, що впали вниз
                }.toMutableList()

                // Генерація нових платформ зверху
                if (newPlatforms.isNotEmpty() && (newPlatforms.last().y > 300f)) {
                    val lastId = newPlatforms.maxOf { it.id }
                    val width = if (Random.nextBoolean()) PLATFORM_LONG else PLATFORM_SHORT
                    newPlatforms.add(
                        Platform(
                            id = lastId + 1,
                            x = Random.nextFloat() * (SCREEN_WIDTH - width),
                            y = newPlatforms.minOf { it.y } - 300f,
                            width = width,
                            height = 75f
                        )
                    )
                }
            }

            // 4. Колізія (Стрибок від платформи)
            if (newVelocityY > 0) { // Тільки якщо падаємо вниз
                state.platforms.forEach { platform ->
                    val playerBottom = newY + 90f // Половина висоти (180 / 2)
                    val playerRight = newX + 70f  // Половина ширини (140 / 2)
                    val playerLeft = newX - 70f

                    if (playerBottom >= platform.y &&
                        playerBottom <= platform.y + platform.height + 20f &&
                        playerRight > platform.x &&
                        playerLeft < platform.x + platform.width
                    ) {
                        newVelocityY = JUMP_STRENGTH
                        newY = platform.y - 90f

                        if (platform.id > newScore || platform.id < newScore) {
                            newScore = platform.id
                        }
                    }
                }
            }

            // 5. Перевірка програшу
            val isGameOver = newY > SCREEN_HEIGHT + 200

            state.copy(
                playerX = newX,
                playerY = newY,
                playerVelocityY = newVelocityY,
                platforms = newPlatforms,
                score = newScore,
                isGameOver = isGameOver,
                isFacingRight = newFacingRight
            )
        }
    }

    // --- УПРАВЛІННЯ ---

    fun onJoystickMoved(percentX: Float, percentY: Float) {
        _gameState.update {
            it.copy(playerVelocityX = percentX * MOVEMENT_SPEED)
        }
    }

    fun togglePause() {
        _gameState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun restartGame() {
        generateInitialPlatforms()
        _gameState.update {
            it.copy(
                playerX = 500f,
                playerY = 1000f,
                playerVelocityY = 0f,
                playerVelocityX = 0f,
                isGameOver = false,
                isPaused = false,
                score = 0
            )
        }
    }

    private fun generateInitialPlatforms() {
        val platforms = mutableListOf<Platform>()
        // Стартова платформа під ногами
        platforms.add(Platform(id = 0, x = 400f, y = 1200f, width = PLATFORM_LONG, height = 75f))

        for (i in 1..10) {
            val width = if (Random.nextBoolean()) PLATFORM_LONG else PLATFORM_SHORT
            platforms.add(
                Platform(
                    id = i,
                    x = Random.nextFloat() * (SCREEN_WIDTH - width),
                    y = 1200f - (i * 300f),
                    width = width,
                    height = 75f
                )
            )
        }
        _gameState.update { it.copy(platforms = platforms) }
    }
}