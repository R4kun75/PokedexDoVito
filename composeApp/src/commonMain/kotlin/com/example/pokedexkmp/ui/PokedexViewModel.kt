package com.example.pokedexkmp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedexkmp.data.PokeApiClient
import com.example.pokedexkmp.data.Pokemon
import com.example.pokedexkmp.data.PokemonMock
import com.example.pokedexkmp.data.local.AppDatabase
import com.example.pokedexkmp.data.local.PokemonCacheEntity
import com.example.pokedexkmp.data.local.PokemonTeamEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PokedexViewModel(private val database: AppDatabase) : ViewModel() {

    // A nossa chave de acesso ao SQLite
    private val pokemonDao = database.pokemonDao()
    private val apiClient = PokeApiClient()

    private val _pokedex = MutableStateFlow<List<Pokemon>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // O filtro paginado agora poderia ser ligado direto ao SQL no futuro,
    // mas mantemos o fluxo reativo para a interface atualizar na hora.
    val filteredPokedex: StateFlow<List<Pokemon>> = combine(_pokedex, _searchQuery) { list, query ->
        if (query.isBlank()) list else list.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _myTeam = MutableStateFlow<List<Pokemon>>(emptyList())
    val myTeam: StateFlow<List<Pokemon>> = _myTeam.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        carregarDadosIniciais()
    }

    private fun carregarDadosIniciais() {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. CARREGA O TIME SALVO NO BANCO
            val timeSalvo = pokemonDao.getTeam().map {
                Pokemon(
                    id = it.id, name = it.name, imageUrl = it.imageUrl,
                    types = listOf("parceiro"), height = 0, weight = 0, stats = emptyList(),
                    description = "Capturado em: ${it.localCaptura}" // A regra da M2 aparecendo aqui!
                )
            }
            _myTeam.value = timeSalvo

            // 2. LÓGICA OFFLINE-FIRST (Cache da Pokédex)
            val cacheCount = pokemonDao.getCacheCount()

            if (cacheCount == 0) {
                // Banco Vazio: Baixa da Internet (API)
                try {
                    val pokemonsDaApi = apiClient.getPokemons(20) // Baixa os 20 primeiros

                    // Converte para a Tabela e Salva no SQLite (ID e Nome apenas)
                    val cacheEntities = pokemonsDaApi.map { PokemonCacheEntity(it.id, it.name) }
                    pokemonDao.insertAllCache(cacheEntities)

                    _pokedex.value = pokemonsDaApi
                    _uiEvent.emit("Dados baixados da API e salvos no banco!")
                } catch (e: Exception) {
                    _pokedex.value = PokemonMock.pokedex
                    _uiEvent.emit("Sem internet. Usando dados locais de emergência.")
                }
            } else {
                // Banco Populado: Lê direto do SQLite sem gastar internet!
                val pokemonsDoBanco = pokemonDao.getPokedexPage(limit = 20, offset = 0, searchQuery = "%%")

                _pokedex.value = pokemonsDoBanco.map {
                    Pokemon(
                        id = it.id, name = it.name,
                        // Reconstrói a URL da imagem baseada no ID salvo
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${it.id}.png",
                        types = listOf("selvagem"), height = 0, weight = 0, stats = emptyList(), description = ""
                    )
                }
                _uiEvent.emit("Modo Offline: Leitura super rápida do Banco de Dados!")
            }

            _isLoading.value = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun addToTeam(pokemon: Pokemon) {
        viewModelScope.launch {
            val currentTeam = _myTeam.value

            if (currentTeam.any { it.id == pokemon.id }) return@launch

            if (currentTeam.size >= 6) {
                _uiEvent.emit("Sua equipe já está cheia! (Máximo de 6 Pokémons)")
                return@launch
            }

            // REGRA DA M2: Salva com o Local de Captura
            val teamEntity = PokemonTeamEntity(
                id = pokemon.id,
                name = pokemon.name,
                imageUrl = pokemon.imageUrl,
                localCaptura = "Rota 1 (Kanto)" // Informação customizada exigida
            )

            // Grava no Banco e depois atualiza a tela
            pokemonDao.insertTeamMember(teamEntity)

            // Atualiza a interface
            val pokemonComLocal = pokemon.copy(description = "Capturado em: ${teamEntity.localCaptura}")
            _myTeam.value = currentTeam + pokemonComLocal
            _uiEvent.emit("${pokemon.name} adicionado ao time e salvo no banco!")
        }
    }

    fun removeFromTeam(pokemon: Pokemon) {
        viewModelScope.launch {
            // Remove do Banco
            val teamEntity = PokemonTeamEntity(pokemon.id, pokemon.name, pokemon.imageUrl, "")
            pokemonDao.removeTeamMember(teamEntity)

            // Atualiza a tela
            _myTeam.value = _myTeam.value.filter { it.id != pokemon.id }
            _uiEvent.emit("${pokemon.name} removido da equipe.")
        }
    }
}