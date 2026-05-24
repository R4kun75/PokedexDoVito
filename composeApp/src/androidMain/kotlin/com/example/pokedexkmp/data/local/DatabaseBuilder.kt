package com.example.pokedexkmp.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    // Garantimos que usamos o contexto global da aplicação para evitar fugas de memória (memory leaks)
    val appContext = context.applicationContext

    // O Android diz-nos qual é o caminho físico seguro para criar o ficheiro
    val dbFile = appContext.getDatabasePath("pokedex.db")

    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}