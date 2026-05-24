package com.example.pokedexkmp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_team")
data class PokemonTeamEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String, // Para mostrar a foto bonitinha no Team Builder

    val localCaptura: String
)