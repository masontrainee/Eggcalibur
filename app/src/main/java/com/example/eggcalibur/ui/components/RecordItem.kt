package com.example.eggcalibur.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eggcalibur.R
import com.example.eggcalibur.data.ScoreRecord

@Composable
fun RecordItem(
    record: ScoreRecord,
    modifier: Modifier = Modifier // Корисно додати modifier, щоб можна було керувати відступами ззовні
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. Картинка-фон плашки
        Image(
            painter = painterResource(id = R.drawable.bg_record_item),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // 2. Текст (Дата і Рахунок)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Дата
            Text(
                text = record.date,
                fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF562716)
            )

            // Рахунок
            Text(
                text = "${record.score}M",
                fontFamily = FontFamily(Font(R.font.montserrat_extrabold)),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF562716)
            )
        }
    }
}