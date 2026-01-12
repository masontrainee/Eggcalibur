package com.example.eggcalibur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.eggcalibur.ui.screen.GameScreen
import com.example.eggcalibur.ui.theme.EggcaliburTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
             EggcaliburTheme {
                GameScreen()
            }
        }
    }
}