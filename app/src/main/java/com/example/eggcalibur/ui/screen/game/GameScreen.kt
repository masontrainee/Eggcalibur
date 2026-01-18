package com.example.eggcalibur.ui.screen.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.eggcalibur.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onExitToMenu: () -> Unit
) {
    val state by viewModel.gameState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                if (!state.isPaused && !state.isGameOver) {
                    viewModel.togglePause()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler(enabled = true) {
        if (!state.isPaused && !state.isGameOver) {
            viewModel.togglePause()
        } else {
            onExitToMenu()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }

        LaunchedEffect(screenWidthPx, screenHeightPx) {
            viewModel.initGame(screenWidthPx, screenHeightPx)
        }

        Box(modifier = Modifier.fillMaxSize()) {

            ActiveGameLayer(
                state = state,
                screenWidthPx = screenWidthPx,
                onPauseClick = { viewModel.togglePause() },
                onJoystickMoved = { x, y -> viewModel.onJoystickMoved(x, y) }
            )

            if (state.isPaused) {
                PauseOverlay(
                    onResume = { viewModel.togglePause() },
                    onExit = onExitToMenu
                )
            }

            if (state.isGameOver) {
                GameOverOverlay(
                    score = state.score,
                    onPlayAgain = { viewModel.restartGame() },
                    onExit = onExitToMenu
                )
            }
        }
    }
}