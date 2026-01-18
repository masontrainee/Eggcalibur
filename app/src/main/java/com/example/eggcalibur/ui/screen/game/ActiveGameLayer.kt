package com.example.eggcalibur.ui.screen.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eggcalibur.R
import com.example.eggcalibur.model.GameState

@Composable
fun ActiveGameLayer(
    state: GameState,
    screenWidthPx: Float,
    onPauseClick: () -> Unit,
    onJoystickMoved: (Float, Float) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        GameCanvas(
            state = state,
            screenWidthPx = screenWidthPx,
            onJoystickMoved = onJoystickMoved
        )

        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .statusBarsPadding()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.btn_pause_icon),
                contentDescription = "Pause",
                modifier = Modifier
                    .size(64.dp)
                    .clickable { onPauseClick() }
            )

            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.bg_score),
                    contentDescription = null,
                    modifier = Modifier.width(106.dp).height(55.dp),
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

@Composable
private fun GameCanvas(
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
        drawImage(
            image = bgImage,
            dstSize = IntSize(size.width.toInt(), size.height.toInt())
        )

        state.platforms.forEach { platform ->
            val threshold = screenWidthPx * 0.15f
            val platformImg = if (platform.width > threshold) platformLongImg else platformShortImg

            drawImage(
                image = platformImg,
                dstOffset = IntOffset(platform.x.toInt(), platform.y.toInt()),
                dstSize = IntSize(platform.width.toInt(), platform.height.toInt())
            )
        }

        val currentChickenSprite = if (state.isFacingRight) chickenRightImg else chickenLeftImg
        drawImage(
            image = currentChickenSprite,
            dstOffset = IntOffset(
                x = (state.playerX - playerWidthPx / 2).toInt(),
                y = (state.playerY - playerHeightPx / 2).toInt()
            ),
            dstSize = IntSize(playerWidthPx.toInt(), playerHeightPx.toInt())
        )
    }
}