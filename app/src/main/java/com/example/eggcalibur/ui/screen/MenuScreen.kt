package com.example.eggcalibur.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.eggcalibur.R
import com.example.eggcalibur.ui.components.GameButton

@Composable
fun MenuScreen(
    onPlayClick: () -> Unit,
    onRecordsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.bg_menu),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-60).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            GameButton(text = "Start", onClick = onPlayClick)

            Spacer(modifier = Modifier.height(16.dp))

            GameButton(text = "Records", onClick = onRecordsClick)

            Spacer(modifier = Modifier.height(16.dp))

            GameButton(text = "Privacy Policy", onClick = onPrivacyClick)
        }

    }
}