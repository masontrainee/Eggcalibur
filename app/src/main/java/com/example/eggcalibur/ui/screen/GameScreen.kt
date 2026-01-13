package com.example.eggcalibur.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eggcalibur.R
import com.example.eggcalibur.viewmodel.GameViewModel

@Preview(showSystemUi = true)
@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val state by viewModel.gameState.collectAsState()

    // 1. ЗАВАНТАЖУЄМО КАРТИНКИ
    val bgImage = ImageBitmap.imageResource(id = R.drawable.bg_game)
    val chickenRightImg = ImageBitmap.imageResource(id = R.drawable.chicken_right)
    val chickenLeftImg = ImageBitmap.imageResource(id = R.drawable.chicken_left)
    val platformLongImg = ImageBitmap.imageResource(id = R.drawable.platform_long)
    val platformShortImg = ImageBitmap.imageResource(id = R.drawable.platform_short)

    val playerWidthPx = 100f
    val playerHeightPx = 180f

    // 2. ЗБЕРІГАЄМО ПОЗИЦІЮ ДОТИКУ
    var touchStartPos by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        touchStartPos = offset
                    },
                    onDrag = { change, _ ->
                        change.consume()

                        val currentPos = change.position
                        // Обчислюємо різницю
                        val dragDisplacement = currentPos.x - touchStartPos.x
                        val sensitivity = 60f
                        val percentX = (dragDisplacement / sensitivity).coerceIn(-1f, 1f)

                        viewModel.onJoystickMoved(percentX, 0f)
                    },
                    onDragEnd = { viewModel.onJoystickMoved(0f, 0f) },
                    onDragCancel = { viewModel.onJoystickMoved(0f, 0f) }
                )
            }
    ) {
        // МАЛЮЄМО ФОН
        drawImage(
            image = bgImage,
            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
        )

        // МАЛЮЄМО ПЛАТФОРМИ
        state.platforms.forEach { platform ->
            val platformImg = if (platform.width > 200f) platformLongImg else platformShortImg
            drawImage(
                image = platformImg,
                dstOffset = IntOffset(platform.x.toInt(), platform.y.toInt()),
                dstSize = IntSize(platform.width.toInt(), platform.height.toInt())
            )
        }

        // МАЛЮЄМО КУРКУ
        val currentChickenSprite = if (state.isFacingRight) chickenRightImg else chickenLeftImg
        drawImage(
            image = currentChickenSprite,
            dstOffset = IntOffset(
                x = (state.playerX - playerWidthPx / 2).toInt(),
                y = (state.playerY - playerHeightPx / 2).toInt()
            ),
            dstSize = IntSize(playerWidthPx.toInt(), playerHeightPx.toInt())
        )

        // GAME OVER
        if (state.isGameOver) {
            drawRect(color = Color.Black.copy(alpha = 0.7f))
        }
    }
}