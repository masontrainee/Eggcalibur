package com.example.eggcalibur.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eggcalibur.R
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onLoadingFinished: () -> Unit) {

    // 1. Таймер на 3 секунди
    LaunchedEffect(Unit) {
        delay(3000)
        onLoadingFinished()
    }

    // 2. Анімація обертання
    val infiniteTransition = rememberInfiniteTransition(label = "gear_spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // ФОН (Включає курку, якщо вона намальована на ньому)
        Image(
            painter = painterResource(id = R.drawable.bg_menu),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ЛОГОТИП
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-100).dp)
                .size(300.dp)
        )

        // БЛОК ЗАВАНТАЖЕННЯ (Шестерня + Текст)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Шестерня
            Image(
                painter = painterResource(id = R.drawable.ic_loading_gear),
                contentDescription = "Loading",
                modifier = Modifier
                    .size(50.dp)
                    .rotate(angle) // Анімація тут
            )

            Spacer(modifier = Modifier.height(10.dp))

            Image(
                painter = painterResource(id = R.drawable.text_loading), // Твій новий файл
                contentDescription = "Loading Text",
                contentScale = ContentScale.Fit, // Зберігає пропорції
                modifier = Modifier.width(120.dp) // Можеш підлаштувати ширину напису
            )
        }
    }
}