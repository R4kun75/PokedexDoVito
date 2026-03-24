package com.example.pokedexkmp.data

data class PokemonStat(
    val name: String,
    val value: Int
)

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val height: Int,
    val weight: Int,
    val stats: List<PokemonStat>,
    val description: String
)