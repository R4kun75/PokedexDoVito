package com.example.pokedexkmp.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object PokedexRoute

@Serializable
data class PokemonDetailRoute(val pokemonId: Int)