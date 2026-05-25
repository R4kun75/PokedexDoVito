package com.example.pokedexkmp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_cache")
data class PokemonCacheEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val types: String,   // Vai guardar algo como "grass,poison"
    val weight: Int,
    val height: Int,
    val stats: String    // Vai guardar algo como "hp:45,attack:49,defense:49"
)