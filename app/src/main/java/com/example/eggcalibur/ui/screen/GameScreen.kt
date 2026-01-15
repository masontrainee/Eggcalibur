@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.eggcalibur.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eggcalibur.R
import com.example.eggcalibur.model.GameState
import com.example.eggcalibur.ui.components.BackButton
import com.example.eggcalibur.ui.components.GameButton
import com.example.eggcalibur.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onExitToMenu: () -> Unit
) {
    val state by viewModel.gameState.collectAsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current

        // Конвертуємо dp (одиниці екрану) в px (пікселі для фізики)
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }

        LaunchedEffect(screenWidthPx, screenHeightPx) {
            viewModel.initGame(screenWidthPx, screenHeightPx)
        }

        Box(modifier = Modifier.fillMaxSize()) {

            GameContent(
                state = state,
                screenWidthPx = screenWidthPx,
                onJoystickMoved = { x, y -> viewModel.onJoystickMoved(x, y) }
            )

            if (!state.isPaused && !state.isGameOver) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .statusBarsPadding()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Кнопка Паузи
                    Image(
                        painter = painterResource(id = R.drawable.btn_pause_icon),
                        contentDescription = "Pause",
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { viewModel.togglePause() }
                    )

                    // Рахунок
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.bg_score),
                            contentDescription = null,
                            modifier = Modifier
                                .width(106.dp)
                                .height(55.dp),
                            contentScale = ContentScale.FillBounds
                        )
                        Text(
                            text = "${state.score}M",
                            fontSize = 22.sp,
                            color = Color(0xFF562716),
                            fontFamily = FontFamily(Font(R.font.montserrat_extrabold))
                        )
                    }
                }
            }
        }
        // ==========================================
        // ШАР 3: ЕКРАН ПАУЗИ
        // ==========================================
        if (state.isPaused) {
            Image(
                painter = painterResource(id = R.drawable.bg_menu),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.header_pause),
                    contentDescription = "Pause Header",
                    modifier = Modifier.width(202.dp).height(61.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(30.dp))
                GameButton(text = "Resume", onClick = { viewModel.togglePause() })
                Spacer(modifier = Modifier.height(16.dp))
                GameButton(text = "Exit", onClick = onExitToMenu)
            }
        }

        // ==========================================
        // ШАР 4: ЕКРАН ПРОГРАШУ
        // ==========================================
        if (state.isGameOver) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.bg_menu),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                BackButton(
                    onBack = onExitToMenu,
                    modifier = Modifier.align(Alignment.TopStart) // Адаптивно притискаємо вліво-вверх
                )

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.header_gameover),
                        contentDescription = "Game Over Header",
                        modifier = Modifier.width(202.dp).height(124.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(25.dp))

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
                            color = Color(0xFF562716)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                    GameButton(text = "Play Again", onClick = { viewModel.restartGame() })
                }
            }
        }
    }
}

@Composable
fun GameContent(
    state: GameState,
    screenWidthPx: Float,
    onJoystickMoved: (Float, Float) -> Unit
) {
    val bgImage = ImageBitmap.imageResource(id = R.drawable.bg_game)
    val chickenRightImg = ImageBitmap.imageResource(id = R.drawable.chicken_right)
    val chickenLeftImg = ImageBitmap.imageResource(id = R.drawable.chicken_left)
    val platformLongImg = ImageBitmap.imageResource(id = R.drawable.platform_long)
    val platformShortImg = ImageBitmap.imageResource(id = R.drawable.platform_short)


    val playerWidthPx = 130f
    val playerHeightPx = 200f
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
            val threshold = screenWidthPx * 0.15f
            val platformImg = if (platform.width > threshold) {
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