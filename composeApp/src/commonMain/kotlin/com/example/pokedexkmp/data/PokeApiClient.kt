package com.example.pokedexkmp.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json

class PokeApiClient {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    // Mantemos a função original caso precise
    suspend fun getPokemons(limit: Int = 20): List<Pokemon> {
        return try {
            val listResponse: PokemonListResponse = httpClient.get("https://pokeapi.co/api/v2/pokemon?limit=$limit").body()
            listResponse.results.mapNotNull { result ->
                val id = result.url.trimEnd('/').substringAfterLast("/").toIntOrNull()
                id?.let { getPokemonDetail(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // NOVA FUNÇÃO: Busca vários IDs paralelamente (Alta Performance)
    suspend fun getPokemonsByIds(ids: List<Int>): List<Pokemon> = coroutineScope {
        ids.map { id ->
            async { getPokemonDetail(id) }
        }.awaitAll().filterNotNull()
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