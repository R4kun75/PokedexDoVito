package com.example.pokedexkmp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_team")
data class PokemonTeamEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val localCaptura: String,
    val types: String,
    val weight: Int,
    val height: Int,
    val stats: String,

    // --- NOVOS CAMPOS PARA A M3 ---
    // Usamos Double? e String? (com interrogação) para permitir valores nulos.
    // Assim, os Pokémons antigos capturados na M2 não causam crash no app!
    val latitude: Double? = null,
    val longitude: Double? = null,
    val photoPath: String? = null
)