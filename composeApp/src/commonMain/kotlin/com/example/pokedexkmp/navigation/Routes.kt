package com.example.pokedexkmp.navigation

import kotlinx.serialization.Serializable

@Serializable
object WelcomeRoute // Nova rota inicial

@Serializable
object PokedexRoute

@Serializable
object TeamBuilderRoute

@Serializable
data class PokemonDetailRoute(val pokemonId: Int)