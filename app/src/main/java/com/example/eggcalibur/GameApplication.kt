package com.example.eggcalibur

import android.app.Application
import com.example.eggcalibur.data.AppDatabase

class GameApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}