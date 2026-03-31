package com.example.pokedexkmp.data

import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(val results: List<PokemonResult>)

@Serializable
data class PokemonResult(val name: String, val url: String)

// Modelos detalhados para ler o JSON completo do Pokémon
@Serializable
data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeSlot>,
    val stats: List<StatSlot>
) {
    // Converte a resposta da API para a classe 'Pokemon' que a sua UI já usa
    fun toPokemon(): Pokemon {
        return Pokemon(
            id = id,
            name = name,
            // A official-artwork tem qualidade HD, perfeito para o design moderno!
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
            types = types.map { it.type.name },
            height = height,
            weight = weight,
            stats = stats.map { PokemonStat(it.stat.name, it.base_stat) },
            // A PokéAPI exige outra chamada só para a descrição, então vamos colocar um texto padrão por enquanto
            description = "Um Pokémon selvagem e misterioso encontrado na região!"
        )
    }
}

@Serializable data class TypeSlot(val type: TypeData)
@Serializable data class TypeData(val name: String)
@Serializable data class StatSlot(val base_stat: Int, val stat: StatData)
@Serializable data class StatData(val name: String)