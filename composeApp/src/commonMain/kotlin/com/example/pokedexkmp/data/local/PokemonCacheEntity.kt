package com.example.pokedexkmp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_cache")
data class PokemonCacheEntity(
    @PrimaryKey
    val id: Int,
    val name: String
)