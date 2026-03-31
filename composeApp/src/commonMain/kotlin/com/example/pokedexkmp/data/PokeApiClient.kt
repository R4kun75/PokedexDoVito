package com.example.pokedexkmp.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PokeApiClient {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Importante para não quebrar se a API mandar campos extras
                coerceInputValues = true
            })
        }
    }

    suspend fun getPokemons(limit: Int = 20): List<Pokemon> {
        return try {
            // 1. Pega a lista básica (nomes e URLs)
            val listResponse: PokemonListResponse = httpClient.get("https://pokeapi.co/api/v2/pokemon?limit=$limit").body()

            // 2. Busca os detalhes de cada um e mapeia direto para nossa classe Pokemon
            listResponse.results.mapNotNull { result ->
                val id = result.url.trimEnd('/').substringAfterLast("/").toIntOrNull()
                id?.let { getPokemonDetail(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun getPokemonDetail(id: Int): Pokemon? {
        return try {
            val response: PokemonDetailResponse = httpClient.get("https://pokeapi.co/api/v2/pokemon/$id").body()
            response.toPokemon()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}