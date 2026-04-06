package com.example.pokedexkmp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedexkmp.data.PokeApiClient
import com.example.pokedexkmp.data.Pokemon
import com.example.pokedexkmp.data.PokemonMock
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PokedexViewModel : ViewModel() {
    private val apiClient = PokeApiClient()

    private val _pokedex = MutableStateFlow<List<Pokemon>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredPokedex: StateFlow<List<Pokemon>> = combine(_pokedex, _searchQuery) { list, query ->
        if (query.isBlank()) list else list.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _myTeam = MutableStateFlow<List<Pokemon>>(emptyList())
    val myTeam: StateFlow<List<Pokemon>> = _myTeam.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // EVENTOS DE UI (Para disparar Snackbars/o aviso de 6 sem prender estado)
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        // Pega a lista do Mock
        val listaMockada = PokemonMock.pokedex

        // Passa a lista para o estado da UI
        _pokedex.value = listaMockada

        // Desliga a roda de loading
        _isLoading.value = false
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun addToTeam(pokemon: Pokemon) {
        _myTeam.update { currentTeam ->
            if (currentTeam.any { it.id == pokemon.id }) {
                currentTeam // Já está no time, não faz nada
            } else if (currentTeam.size >= 6) {
                // Time cheio! Dispara o evento de aviso
                viewModelScope.launch {
                    _uiEvent.emit("Sua equipe já está cheia! (Máximo de 6 Pokémons)")
                }
                currentTeam
            } else {
                currentTeam + pokemon // Adiciona com sucesso
            }
        }
    }

    fun removeFromTeam(pokemon: Pokemon) {
        _myTeam.update { currentTeam -> currentTeam.filter { it.id != pokemon.id } }
    }
}