package com.example.eggcalibur.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eggcalibur.R

@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val btnFont = FontFamily(Font(R.font.montserrat_bold))
    // Box дозволяє накласти Текст поверх Картинки кнопки
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(70.dp)
            .width(250.dp)
            .clickable { onClick() }
    ) {
        // 1. Картинка кнопки
        Image(
            painter = painterResource(id = R.drawable.btn_bg_blue),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        // 2. Текст кнопки
        Text(
            text = text,
            color = Color(0xFFFFC300),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = btnFont
        )
    }
}