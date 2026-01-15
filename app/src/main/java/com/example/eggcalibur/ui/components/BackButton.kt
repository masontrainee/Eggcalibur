package com.example.eggcalibur.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.eggcalibur.R

@Composable
fun BackButton(
    onBack: () -> Unit,
    modifier: Modifier = Modifier // Дозволяє нам передати "align" з батьківського екрану
) {
    Box(
        modifier = modifier
            .padding(top = 40.dp, start = 24.dp) // Стандартний відступ для всіх екранів
            .size(55.dp) // Стандартний розмір
            .clickable { onBack() }
    ) {
        Image(
            painter = painterResource(id = R.drawable.btn_back),
            contentDescription = "Back",
            modifier = Modifier.fillMaxSize()
        )
    }
}