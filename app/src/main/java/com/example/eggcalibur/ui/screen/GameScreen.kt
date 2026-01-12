package com.example.eggcalibur.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eggcalibur.viewmodel.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val state by viewModel.gameState.collectAsState()

    // Змінна, щоб запам'ятати, де гравець поставив палець
    var touchStartPos by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            // Цей блок відповідає за обробку дотиків по всьому екрану
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Запам'ятовуємо точку старту дотику
                        touchStartPos = offset
                    },
                    onDrag = { change, _ ->
                        change.consume() // Позначаємо подію як оброблену

                        // Рахуємо, наскільки далеко палець відійшов від точки старту
                        val currentPos = change.position
                        val dragDisplacement = currentPos.x - touchStartPos.x

                        // Чутливість: 150 пікселів зсуву = максимальна швидкість
                        val sensitivity = 150f

                        // Перетворюємо дистанцію в відсотки (-1.0 ... 1.0)
                        val percentX = (dragDisplacement / sensitivity).coerceIn(-1f, 1f)

                        // Передаємо у ViewModel (Y нам не потрібен для руху вліво-вправо)
                        viewModel.onJoystickMoved(percentX, 0f)
                    },
                    onDragEnd = {
                        // Коли відпустили палець - зупиняємо рух
                        viewModel.onJoystickMoved(0f, 0f)
                    },
                    onDragCancel = {
                        viewModel.onJoystickMoved(0f, 0f)
                    }
                )
            }
    ) {
        // 1. Малюємо Платформи
        state.platforms.forEach { platform ->
            // Різний колір: Довгі - зелені, Короткі - помаранчеві
            val color = if (platform.width > 200f) Color.Green else Color(0xFFFFA500)

            drawRect(
                color = color,
                topLeft = Offset(x = platform.x, y = platform.y),
                size = Size(width = platform.width, height = platform.height)
            )
        }

        // 2. Малюємо Курку
        drawCircle(
            color = Color.Yellow,
            radius = 50f,
            center = Offset(x = state.playerX, y = state.playerY)
        )

        // 3. Game Over (напівпрозорий чорний фон)
        if (state.isGameOver) {
            drawRect(color = Color.Black.copy(alpha = 0.5f))
        }
    }
}