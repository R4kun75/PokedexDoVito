package com.example.pokedexkmp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedexkmp.data.PokeApiClient
import com.example.pokedexkmp.data.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokedexViewModel : ViewModel() {
    private val apiClient = PokeApiClient()

    // Estado da Pokédex (Lista da API)
    private val _pokedex = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokedex: StateFlow<List<Pokemon>> = _pokedex.asStateFlow()

    // Estado do Time (Pokémons capturados)
    private val _myTeam = MutableStateFlow<List<Pokemon>>(emptyList())
    val myTeam: StateFlow<List<Pokemon>> = _myTeam.asStateFlow()

    // Estado de Carregamento
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchPokemons()
    }

    private fun fetchPokemons() {
        viewModelScope.launch {
            _isLoading.value = true
            val pokemons = apiClient.getPokemons(limit = 10)
            _pokedex.value = pokemons
            _isLoading.value = false
        }
    }

    fun addToTeam(pokemon: Pokemon) {
        _myTeam.update { currentTeam ->
            // Regra: Máximo de 6 Pokémons no time e não pode repetir
            if (currentTeam.size < 6 && !currentTeam.any { it.id == pokemon.id }) {
                currentTeam + pokemon
            } else {
                currentTeam
            }
        }
    }

    fun removeFromTeam(pokemon: Pokemon) {
        _myTeam.update { currentTeam ->
            currentTeam.filter { it.id != pokemon.id }
        }
    }
}