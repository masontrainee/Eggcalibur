package com.example.eggcalibur.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eggcalibur.data.ScoreDao
import com.example.eggcalibur.data.ScoreRecord
import com.example.eggcalibur.model.GameState
import com.example.eggcalibur.model.Platform
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class GameViewModel(private val scoreDao: ScoreDao) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    private var screenWidth = 0f
    private var screenHeight = 0f
    private var platformHeight = 0f
    private var platformShort = 0f
    private var platformLong = 0f
    private var platformVerticalSpacing = 0f
    private var gravity = 0f
    private var jumpStrength = 0f
    private var movementSpeed = 0f
    private var isScoreSaved = false
    private var isGameInitialized = false

    fun initGame(width: Float, height: Float) {
        if (width <= 0f || height <= 0f) return

        if (isGameInitialized && screenWidth == width && screenHeight == height) return

        val isFirstStart = !isGameInitialized

        screenWidth = width
        screenHeight = height

        platformShort = screenWidth * 0.1f  // Коротка платформа = 10% ширини
        platformLong = screenWidth * 0.2f   // Довга платформа = 20% ширини
        platformHeight = screenHeight * 0.03f
        platformVerticalSpacing = screenHeight * 0.15f
        movementSpeed = screenWidth * 0.025f // Швидкість руху

        gravity = screenHeight * 0.0005f     // Гравітація залежить від висоти
        jumpStrength = -(screenHeight * 0.015f) // Сила стрибка

        isGameInitialized = true
        restartGame()
        if (isFirstStart) {
            startGameLoop()
        }
    }

    override fun onCleared() {
        super.onCleared()
        val currentScore = _gameState.value.score
        if (currentScore > 0 && !isScoreSaved) {
            saveScoreToDb(currentScore)
        }
    }

    fun onJoystickMoved(percentX: Float, percentY: Float) {
        if (!isGameInitialized) return
        _gameState.update {
            it.copy(playerVelocityX = percentX * movementSpeed)
        }
    }

    fun togglePause() {
        _gameState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun restartGame() {
        if (!isGameInitialized) return

        val currentScore = _gameState.value.score
        if (currentScore > 0 && !isScoreSaved) {
            saveScoreToDb(currentScore)
        }

        isScoreSaved = false
        generateInitialPlatforms()

        // Ставимо гравця по центру і знизу
        val startX = screenWidth / 2
        val startY = screenHeight * 0.8f

        _gameState.update {
            it.copy(
                playerX = startX,
                playerY = startY - (screenHeight * 0.15f),
                playerVelocityY = 0f,
                playerVelocityX = 0f,
                isGameOver = false,
                isPaused = false,
                score = 0
            )
        }
    }

    private fun saveScoreToDb(score: Int) {
        if (isScoreSaved) return
        isScoreSaved = true
        viewModelScope.launch {
            val currentDate = SimpleDateFormat("dd.MM", Locale.getDefault()).format(Date())
            scoreDao.insert(ScoreRecord(score = score, date = currentDate))
        }
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            while (true) {
                if (isGameInitialized && !_gameState.value.isPaused && !_gameState.value.isGameOver) {
                    updatePhysics()
                }
                delay(16L)
            }
        }
    }

    private fun updatePhysics() {
        _gameState.update { state ->
            var newX = state.playerX + state.playerVelocityX
            val playerRadius = screenWidth * 0.05f

            var newFacingRight = state.isFacingRight
            if (state.playerVelocityX > 0.1f) newFacingRight = true
            else if (state.playerVelocityX < -0.1f) newFacingRight = false

            if (newX > screenWidth) newX = -playerRadius
            else if (newX < -playerRadius) newX = screenWidth

            // 2. Рух по Y
            var newVelocityY = state.playerVelocityY + gravity
            var newY = state.playerY + newVelocityY

            var newPlatforms = state.platforms
            var newScore = state.score

            val cameraThreshold = screenHeight * 0.4f

            if (newY < cameraThreshold) {
                val diff = cameraThreshold - newY
                newY = cameraThreshold

                newPlatforms = state.platforms.map {
                    it.copy(y = it.y + diff)
                }.filter {
                    it.y < screenHeight + 100
                }.toMutableList()

                // Генерація нових платформ зверху
                if (newPlatforms.isNotEmpty()) {
                    val highestPlatformY = newPlatforms.minOf { it.y }

                    if (highestPlatformY > 0) {
                        val lastId = newPlatforms.maxOf { it.id }
                        val width = if (Random.nextBoolean()) platformLong else platformShort
                        newPlatforms.add(
                            Platform(
                                id = lastId + 1,
                                x = Random.nextFloat() * (screenWidth - width),
                                y = highestPlatformY - platformVerticalSpacing,
                                width = width,
                                height = platformHeight
                            )
                        )
                    }
                }
            }

            // 4. Колізія
            if (newVelocityY > 0) {
                state.platforms.forEach { platform ->
                    val pBottom = newY + (screenWidth * 0.08f)
                    val pRight = newX + (screenWidth * 0.05f)
                    val pLeft = newX - (screenWidth * 0.05f)

                    if (pBottom >= platform.y &&
                        pBottom <= platform.y + platform.height + (screenHeight * 0.02f) && // Допуск
                        pRight > platform.x &&
                        pLeft < platform.x + platform.width
                    ) {
                        newVelocityY = jumpStrength
                        newY = platform.y - (screenWidth * 0.08f)

                        if (platform.id > newScore || platform.id < newScore) {
                            newScore = platform.id
                        }
                    }
                }
            }

            val isNowGameOver = newY > screenHeight + (screenHeight * 0.1f)

            if (isNowGameOver && !state.isGameOver) {
                if (newScore > 0) saveScoreToDb(newScore)
            }

            state.copy(
                playerX = newX,
                playerY = newY,
                playerVelocityY = newVelocityY,
                platforms = newPlatforms,
                score = newScore,
                isGameOver = isNowGameOver,
                isFacingRight = newFacingRight
            )
        }
    }

    private fun generateInitialPlatforms() {
        val platforms = mutableListOf<Platform>()
        val startY = screenHeight * 0.8f
        val startX = (screenWidth - platformLong) / 2

        platforms.add(Platform(0, startX, startY, platformLong, platformHeight))

        for (i in 1..6) {
            val width = if (Random.nextBoolean()) platformLong else platformShort
            platforms.add(
                Platform(
                    id = i,
                    x = Random.nextFloat() * (screenWidth - width),
                    y = startY - (i * platformVerticalSpacing),
                    width = width,
                    height = platformHeight
                )
            )
        }

        _gameState.update { it.copy(platforms = platforms) }
    }
}

class GameViewModelFactory(private val scoreDao: ScoreDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(scoreDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}