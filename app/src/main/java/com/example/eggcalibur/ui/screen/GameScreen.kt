package com.example.eggcalibur.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eggcalibur.R
import com.example.eggcalibur.model.GameState
import com.example.eggcalibur.ui.components.GameButton
import com.example.eggcalibur.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    onExitToMenu: () -> Unit
) {
    val state by viewModel.gameState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        // ==========================================
        // ШАР 1: ГРА (GAMEPLAY)
        // ==========================================
        GameContent(
            state = state,
            onJoystickMoved = { x, y -> viewModel.onJoystickMoved(x, y) }
        )

        // ==========================================
        // ШАР 2: КНОПКА ПАУЗИ (В ГРІ)
        // ==========================================
        if (!state.isPaused && !state.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp, start = 24.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.btn_pause_icon),
                    contentDescription = "Pause",
                    modifier = Modifier
                        .size(64.dp)
                        .clickable { viewModel.togglePause() }
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, end = 24.dp), // Відступ справа
            contentAlignment = Alignment.TopEnd // Притискаємо вправо
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                // 1. Картинка-фон (табличка)
                Image(
                    painter = painterResource(id = R.drawable.bg_score), // Та сама картинка, що і в Game Over
                    contentDescription = null,
                    modifier = Modifier
                        .width(120.dp) // Трохи менша, ніж у Game Over
                        .height(50.dp),
                    contentScale = ContentScale.FillBounds
                )

                // 2. Текст рахунку (по центру картинки)
                Text(
                    text = "${state.score}M",
                    fontSize = 22.sp, // Розмір шрифту
                    color = Color(0xFF562716), // Твій коричневий колір
                    modifier = Modifier.align(Alignment.Center),
                    fontFamily = FontFamily(Font(R.font.montserrat_extrabold))
                )
            }
        }
    }
        // ==========================================
        // ШАР 3: ЕКРАН ПАУЗИ (PAUSE MENU)
        // ==========================================
        if (state.isPaused) {
            // Фон меню
            Image(
                painter = painterResource(id = R.drawable.bg_menu),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Вміст по центру
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Картинка "PAUSE"
                Image(
                    painter = painterResource(id = R.drawable.header_pause),
                    contentDescription = "Pause Header",
                    modifier = Modifier.width(202.dp).height(61.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Кнопка Resume
                GameButton(text = "Resume", onClick = { viewModel.togglePause() })

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка Exit (Широка синя кнопка, як на макеті)
                GameButton(text = "Exit", onClick = onExitToMenu)
            }
        }

        // ==========================================
        // ШАР 4: ЕКРАН ПРОГРАШУ (GAME OVER)
        // ==========================================
        if (state.isGameOver) {
            Box(modifier = Modifier.fillMaxSize()) {
                // 1. Фон
                Image(
                    painter = painterResource(id = R.drawable.bg_menu),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 2. Кнопка "НАЗАД" (Жовта стрілка зліва зверху)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 40.dp, start = 24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.btn_back),
                        contentDescription = "Exit",
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { onExitToMenu() }
                    )
                }

                // 3. Центр (Заголовок, Рахунок, Play Again)
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Картинка "GAME OVER"
                    Image(
                        painter = painterResource(id = R.drawable.header_gameover),
                        contentDescription = "Game Over Header",
                        modifier = Modifier.width(202.dp).height(124.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Табличка з рахунком
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.bg_score),
                            contentDescription = null,
                            modifier = Modifier.width(106.dp).height(55.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = "${state.score}M",
                            fontSize = 25.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
                            color = Color(0xFF562716) // Коричневий колір тексту
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Кнопка Play Again
                    GameButton(text = "Play Again", onClick = { viewModel.restartGame() })
                }
            }
        }
    }


// ==========================================
// ЛОГІКА МАЛЮВАННЯ (З форматуванням)
// ==========================================
@Composable
fun GameContent(
    state: GameState,
    onJoystickMoved: (Float, Float) -> Unit
) {
    val bgImage = ImageBitmap.imageResource(id = R.drawable.bg_game)
    val chickenRightImg = ImageBitmap.imageResource(id = R.drawable.chicken_right)
    val chickenLeftImg = ImageBitmap.imageResource(id = R.drawable.chicken_left)
    val platformLongImg = ImageBitmap.imageResource(id = R.drawable.platform_long)
    val platformShortImg = ImageBitmap.imageResource(id = R.drawable.platform_short)

    val playerWidthPx = 100f
    val playerHeightPx = 180f
    var touchStartPos by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { touchStartPos = it },
                    onDrag = { change, _ ->
                        change.consume()
                        val dragDisplacement = change.position.x - touchStartPos.x
                        val sensitivity = 60f
                        val percentX = (dragDisplacement / sensitivity).coerceIn(-1f, 1f)
                        onJoystickMoved(percentX, 0f)
                    },
                    onDragEnd = { onJoystickMoved(0f, 0f) },
                    onDragCancel = { onJoystickMoved(0f, 0f) }
                )
            }
    ) {
        // 1. ФОН
        drawImage(
            image = bgImage,
            dstSize = IntSize(
                width = size.width.toInt(),
                height = size.height.toInt()
            )
        )

        // 2. ПЛАТФОРМИ
        state.platforms.forEach { platform ->
            val platformImg = if (platform.width > 199f) {
                platformLongImg
            } else platformShortImg

            drawImage(
                image = platformImg,
                dstOffset = IntOffset(
                    x = platform.x.toInt(),
                    y = platform.y.toInt()
                ),
                dstSize = IntSize(
                    width = platform.width.toInt(),
                    height = platform.height.toInt()
                )
            )
        }

        // 3. КУРКА
        val currentChickenSprite = if (state.isFacingRight) chickenRightImg else chickenLeftImg

        drawImage(
            image = currentChickenSprite,
            dstOffset = IntOffset(
                x = (state.playerX - playerWidthPx / 2).toInt(),
                y = (state.playerY - playerHeightPx / 2).toInt()
            ),
            dstSize = IntSize(
                width = playerWidthPx.toInt(),
                height = playerHeightPx.toInt()
            )
        )
    }
}