package com.example.pokedexkmp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// A versão muda para 2! O Room KMP vai gerar um arquivo "2.json" na sua pasta schemas.
@Database(
    entities = [
        PokemonCacheEntity::class,
        PokemonTeamEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    // Esta função é a única porta de entrada para fazer pesquisas no banco
    abstract fun pokemonDao(): PokemonDao

}