package com.example.pokedexkmp.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    // A Apple nos dá o diretório isolado (Sandbox) onde podemos gravar arquivos com segurança
    val dbFilePath = NSHomeDirectory() + "/pokedex.db"

    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath,
        factory = { AppDatabase::class.instantiateImpl() }
    )
}