package com.example.eggcalibur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eggcalibur.ui.screen.GameScreen
import com.example.eggcalibur.ui.screen.LoadingScreen
import com.example.eggcalibur.ui.screen.MenuScreen
import com.example.eggcalibur.ui.theme.EggcaliburTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EggcaliburTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "loading") {

                    // 1. ЕКРАН ЗАВАНТАЖЕННЯ
                    composable("loading") {
                        LoadingScreen(
                            onLoadingFinished = {
                                // Видаляємо екран завантаження з історії, щоб "Назад" не повертало на нього
                                navController.navigate("menu") {
                                    popUpTo("loading") { inclusive = true }
                                }
                            }
                        )
                    }
                    // МЕНЮ
                    composable("menu") {
                        MenuScreen(
                            onPlayClick = { navController.navigate("game") },
                            onRecordsClick = { /* Поки пустo */ },
                            onPrivacyClick = { /* Поки пустo */ }
                        )
                    }

                    // ГРА
                    composable("game") {GameScreen(
                        onExitToMenu = {
                            // Ця команда повертає користувача назад у меню
                            navController.navigate("menu") {
                                // Видаляємо гру з історії, щоб кнопка "Назад" не повертала в гру
                                popUpTo("menu") { inclusive = true }
                            }
                        }
                    )
                    }

                    // Тут пізніше додамо "records" і "privacy"
                }
            }
        }
    }
}