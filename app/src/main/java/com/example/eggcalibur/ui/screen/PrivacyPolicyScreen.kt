package com.example.eggcalibur.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.eggcalibur.R
import com.example.eggcalibur.ui.components.BackButton

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    val fontBold = FontFamily(Font(R.font.montserrat_bold))

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Фон
        Image(
            painter = painterResource(id = R.drawable.bg_menu2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.15f))

            Image(
                painter = painterResource(id = R.drawable.privacy_policy),
                contentDescription = "Records Header",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(225.dp).height(18.dp),
            )

        }

        BackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}