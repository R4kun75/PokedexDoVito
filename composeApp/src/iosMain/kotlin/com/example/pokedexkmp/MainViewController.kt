package com.example.pokedexkmp

import androidx.compose.ui.window.ComposeUIViewController
// NOVO IMPORT AQUI EMBAIXO:
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.pokedexkmp.data.local.getDatabaseBuilder

fun MainViewController() = ComposeUIViewController {

    // INSTALANDO O MOTOR NO IOS
    val database = getDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .build()

    App(database = database)
}