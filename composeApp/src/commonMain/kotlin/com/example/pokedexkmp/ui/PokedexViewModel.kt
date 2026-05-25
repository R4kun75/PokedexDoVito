package com.example.pokedexkmp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedexkmp.data.PokeApiClient
import com.example.pokedexkmp.data.Pokemon
import com.example.pokedexkmp.data.PokemonMock
import com.example.pokedexkmp.data.PokemonStat
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
    private val _selectedType = MutableStateFlow("")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

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

    private var currentOffset = 0
    private var isPaginating = false

    init {
        carregarDadosIniciais()
    }

    private fun carregarDadosIniciais() {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. CARREGA O TIME COM AS CORES E STATUS REAIS
            val timeSalvo = pokemonDao.getTeam().map { entity ->
                Pokemon(
                    id = entity.id, name = entity.name, imageUrl = entity.imageUrl,
                    types = entity.types.split(","), // Transforma o texto de volta em Lista
                    height = entity.height,
                    weight = entity.weight,
                    stats = if (entity.stats.isBlank()) emptyList() else entity.stats.split(",")
                        .map { statStr ->
                            val parts = statStr.split(":")
                            PokemonStat(parts[0], parts[1].toInt())
                        },
                    description = "Capturado em: ${entity.localCaptura}"
                )
            }
            _myTeam.value = timeSalvo

            // 2. TENTA INTERNET, SE FALHAR LÊ O CACHE COM DADOS COMPLETOS
            try {
                val pokemonsDaApi = apiClient.getPokemons(20)
                if (pokemonsDaApi.isEmpty()) throw Exception("Erro de rede")

                // Grava no banco transformando as listas em Textos simples separados por vírgula
                val cacheEntities = pokemonsDaApi.map {
                    PokemonCacheEntity(
                        id = it.id,
                        name = it.name,
                        types = it.types.joinToString(","),
                        weight = it.weight,
                        height = it.height,
                        stats = it.stats.joinToString(",") { stat -> "${stat.name}:${stat.value}" }
                    )
                }
                pokemonDao.insertAllCache(cacheEntities)

                _pokedex.value = pokemonsDaApi
                _uiEvent.emit("Dados atualizados da API!")

            } catch (e: Exception) {
                // MODO OFFLINE: Lê o cache e reconstrói o Pokémon inteiro com as cores originais!
                val pokemonsDoBanco =
                    pokemonDao.getPokedexPage(limit = 20, offset = 0, searchQuery = "%%")

                _pokedex.value = pokemonsDoBanco.map { entity ->
                    Pokemon(
                        id = entity.id, name = entity.name,
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${entity.id}.png",
                        types = entity.types.split(","),
                        height = entity.height,
                        weight = entity.weight,
                        stats = if (entity.stats.isBlank()) emptyList() else entity.stats.split(",")
                            .map { statStr ->
                                val parts = statStr.split(":")
                                PokemonStat(parts[0], parts[1].toInt())
                            },
                        description = "Modo Offline ativo."
                    )
                }
                _uiEvent.emit("Sem internet: Pokédex carregada do SQLite!")
            }

            _isLoading.value = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onTypeSelected(type: String) {
        if (_selectedType.value == type) {
            _selectedType.value = "" // Clica de novo para remover o filtro
        } else {
            _selectedType.value = type
        }
        resetAndLoad()
    }

    override fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        resetAndLoad()
    }

    private fun resetAndLoad() {
        currentOffset = 0
        _pokedex.value = emptyList() // Limpa a tela
        carregarMaisPokemons() // Carrega a página 0 com os novos filtros
    }

    fun addToTeam(pokemon: Pokemon, localCaptura: String) { // <-- Novo parâmetro aqui!
        viewModelScope.launch {
            val currentTeam = _myTeam.value
            if (currentTeam.any { it.id == pokemon.id }) return@launch

            if (currentTeam.size >= 6) {
                _uiEvent.emit("Sua equipe já está cheia! (Máximo de 6 Pokémons)")
                return@launch
            }

            // Grava a Entidade do Time com o texto que o usuário digitou!
            val teamEntity = PokemonTeamEntity(
                id = pokemon.id,
                name = pokemon.name,
                imageUrl = pokemon.imageUrl,
                localCaptura = localCaptura, // <-- Usando a variável
                types = pokemon.types.joinToString(","),
                weight = pokemon.weight,
                height = pokemon.height,
                stats = pokemon.stats.joinToString(",") { stat -> "${stat.name}:${stat.value}" }
            )

            pokemonDao.insertTeamMember(teamEntity)

            val pokemonComLocal = pokemon.copy(description = "Capturado em: ${teamEntity.localCaptura}")
            _myTeam.value = currentTeam + pokemonComLocal
            _uiEvent.emit("${pokemon.name} adicionado ao time!")
        }
    }

    fun removeFromTeam(pokemon: Pokemon) {
        viewModelScope.launch {
            // Preenchemos com dados vazios só para satisfazer o Kotlin,
            // pois o Room vai usar apenas o 'id' para achar e deletar no banco.
            val teamEntity = PokemonTeamEntity(
                id = pokemon.id,
                name = pokemon.name,
                imageUrl = pokemon.imageUrl,
                localCaptura = "",
                types = "",
                weight = 0,
                height = 0,
                stats = ""
            )
            pokemonDao.removeTeamMember(teamEntity)

            // Atualiza a tela
            _myTeam.value = _myTeam.value.filter { it.id != pokemon.id }
            _uiEvent.emit("${pokemon.name} removido da equipe.")
        }
    }

    fun carregarMaisPokemons() {
        if (isPaginating) return

        viewModelScope.launch {
            isPaginating = true

            val currentSearch = _searchQuery.value
            val currentType = _selectedType.value

            try {
                // REGRA DE NEGÓCIO: Se o usuário está filtrando, lemos APENAS do Banco Local!
                if (currentSearch.isNotEmpty() || currentType.isNotEmpty()) {
                    val novosDoBanco = pokemonDao.getPokedexPage(
                        limit = 20, offset = currentOffset,
                        searchQuery = currentSearch, typeFilter = currentType
                    )

                    val convertidos = novosDoBanco.map { entity ->
                        Pokemon(
                            id = entity.id, name = entity.name,
                            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${entity.id}.png",
                            types = entity.types.split(","),
                            height = entity.height, weight = entity.weight,
                            stats = if (entity.stats.isBlank()) emptyList() else entity.stats.split(",").map { statStr ->
                                val parts = statStr.split(":")
                                PokemonStat(parts[0], parts[1].toInt())
                            },
                            description = "Filtro Local Ativo."
                        )
                    }
                    _pokedex.value = _pokedex.value + convertidos

                } else {
                    // Sem filtros? Tenta puxar a próxima página da Internet normalmente
                    val novosPokemonsApi = apiClient.getPokemons(limit = 20, offset = currentOffset)
                    if (novosPokemonsApi.isEmpty()) throw Exception("Erro de rede")

                    val cacheEntities = novosPokemonsApi.map {
                        com.example.pokedexkmp.data.local.PokemonCacheEntity(
                            id = it.id, name = it.name,
                            types = it.types.joinToString(","),
                            weight = it.weight, height = it.height,
                            stats = it.stats.joinToString(",") { stat -> "${stat.name}:${stat.value}" }
                        )
                    }
                    pokemonDao.insertAllCache(cacheEntities)
                    _pokedex.value = _pokedex.value + novosPokemonsApi
                }
                currentOffset += 20

            } catch (e: Exception) {
                // Falha de Internet no carregamento normal: Cai para o Banco Local
                val novosDoBanco = pokemonDao.getPokedexPage(
                    limit = 20, offset = currentOffset,
                    searchQuery = "", typeFilter = ""
                )
                if (novosDoBanco.isNotEmpty()) {
                    val convertidos = novosDoBanco.map { entity ->
                        // ... (mesma conversão de entidade para Pokemon que você já tem no catch)
                        Pokemon(
                            id = entity.id, name = entity.name,
                            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${entity.id}.png",
                            types = entity.types.split(","),
                            height = entity.height, weight = entity.weight,
                            stats = if (entity.stats.isBlank()) emptyList() else entity.stats.split(",").map { statStr ->
                                val parts = statStr.split(":")
                                PokemonStat(parts[0], parts[1].toInt())
                            },
                            description = "Modo Offline ativo."
                        )
                    }
                    _pokedex.value = _pokedex.value + convertidos
                    currentOffset += 20
                }
            }
            isPaginating = false
        }
    }

}