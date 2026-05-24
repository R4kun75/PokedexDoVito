package com.example.pokedexkmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
// NOVO IMPORT AQUI EMBAIXO:
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.pokedexkmp.data.local.getDatabaseBuilder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // INSTALANDO O MOTOR DO SQLITE ANTES DO BUILD!
        val database = getDatabaseBuilder(applicationContext)
            .setDriver(BundledSQLiteDriver())
            .build()

        setContent {
            App(database = database)
        }
    }
}