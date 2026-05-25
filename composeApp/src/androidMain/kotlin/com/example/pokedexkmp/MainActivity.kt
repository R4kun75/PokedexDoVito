package com.example.pokedexkmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.pokedexkmp.data.local.getDatabaseBuilder
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Sem o EdgeToEdge para não bugar a tela

        // 1. Ligamos o motor do SQLite em Background
        val database = getDatabaseBuilder(applicationContext)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

        setContent {
            // 2. Injetamos o banco para dentro do Compose
            App(database = database)
        }
    }
}