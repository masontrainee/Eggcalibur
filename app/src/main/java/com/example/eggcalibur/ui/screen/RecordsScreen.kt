package com.example.eggcalibur.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.eggcalibur.GameApplication
import com.example.eggcalibur.R
import com.example.eggcalibur.ui.components.BackButton
import com.example.eggcalibur.ui.components.RecordItem

@Composable
fun RecordsScreen(onBack: () -> Unit) {
    // 1. Отримуємо доступ до бази даних
    val context = LocalContext.current
    val app = context.applicationContext as GameApplication
    val dao = app.database.scoreDao()

    // 2. Читаємо список рекордів (Flow перетворюється на State)
    val records by dao.getTopScores().collectAsState(initial = emptyList())

    // Головний контейнер
    Box(modifier = Modifier.fillMaxSize()) {

        // ШАР 1: Фон
        Image(
            painter = painterResource(id = R.drawable.bg_menu2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ШАР 2: Основний контент
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Відступ зверху для заголовка
            Spacer(modifier = Modifier.fillMaxHeight(0.15f))

            // Картинка-Заголовок "RECORDS"
            Image(
                painter = painterResource(id = R.drawable.header_records),
                contentDescription = "Records Header",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(130.dp).height(18.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Список рекордів
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Займає все вільне місце, що залишилось
                    .padding(horizontal = 24.dp), // Відступи з боків
                verticalArrangement = Arrangement.spacedBy(16.dp), // Відстань між плашками
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 50.dp) // Відступ знизу, щоб не перекривалось куркою
            ) {
                itemsIndexed(records) { index, record ->
                    RecordItem(record = record)
                }
            }
        }

        // ШАР 3: Кнопка Назад
        BackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}
