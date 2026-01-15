package com.example.eggcalibur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eggcalibur.ui.screen.GameScreen
import com.example.eggcalibur.ui.screen.LoadingScreen
import com.example.eggcalibur.ui.screen.MenuScreen
import com.example.eggcalibur.ui.screen.PrivacyPolicyScreen
import com.example.eggcalibur.ui.screen.RecordsScreen
import com.example.eggcalibur.ui.theme.EggcaliburTheme
import com.example.eggcalibur.viewmodel.GameViewModel
import com.example.eggcalibur.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EggcaliburTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "loading") {

                    composable("loading") {
                        LoadingScreen(
                            onLoadingFinished = {
                                navController.navigate("menu") {
                                    popUpTo("loading") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("menu") {
                        MenuScreen(
                            onPlayClick = { navController.navigate("game") },
                            onRecordsClick = { navController.navigate("records") },
                            onPrivacyClick = { navController.navigate("privacy") }
                        )
                    }

                    composable("game") {
                        val context = LocalContext.current
                        val app = context.applicationContext as GameApplication
                        val db = app.database

                        val viewModel: GameViewModel = viewModel(
                            factory = GameViewModelFactory(db.scoreDao())
                        )

                        GameScreen(
                            viewModel = viewModel,
                            onExitToMenu = {
                                navController.navigate("menu") {
                                    popUpTo("menu") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("records") {
                        RecordsScreen(onBack = { navController.popBackStack() })
                    }

                    composable("privacy") {
                        PrivacyPolicyScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}