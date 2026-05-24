package com.example.pokedexkmp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Aqui nós "registramos" as duas tabelas que criamos e definimos a versão do banco.
// Se no futuro você adicionar uma nova tabela, a versão muda para 2.
@Database(
    entities = [
        PokemonCacheEntity::class,
        PokemonTeamEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    // Esta função é a única porta de entrada para fazer pesquisas no banco
    abstract fun pokemonDao(): PokemonDao

}