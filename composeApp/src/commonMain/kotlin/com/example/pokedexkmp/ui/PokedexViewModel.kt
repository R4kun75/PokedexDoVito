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
    private val apiClient = PokeApiClient()
    private val pokemonDao = database.pokemonDao()

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

    // Estado para guardar o Pokémon selecionado AO VIVO da API
    private val _selectedPokemonDetails = MutableStateFlow<Pokemon?>(null)
    val selectedPokemonDetails: StateFlow<Pokemon?> = _selectedPokemonDetails.asStateFlow()

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
                // Usando o nome correto: pokemonsDoBanco e passando os 4 parâmetros!
                val pokemonsDoBanco = pokemonDao.getPokedexPage(
                    limit = 20, offset = currentOffset,
                    searchQuery = "", typeFilter = ""
                )

                if (pokemonsDoBanco.isNotEmpty()) {
                    val convertidos = pokemonsDoBanco.map { entity ->
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

        }
    }

    fun onTypeSelected(type: String) {
        if (_selectedType.value == type) {
            _selectedType.value = "" // Clica de novo para remover o filtro
        } else {
            _selectedType.value = type
        }
        resetAndLoad()
    }

     fun onSearchQueryChanged(query: String) {
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
            _isLoading.value = _pokedex.value.isEmpty()

            val currentSearch = _searchQuery.value
            val currentType = _selectedType.value

            try {
                // 1. REGRA DE OURO DO PROFESSOR: Consulta o banco de dados local PRIMEIRO!
                val bancoLocal = pokemonDao.getPokedexPage(
                    limit = 20, offset = currentOffset,
                    searchQuery = currentSearch, typeFilter = currentType
                )

                // 2. Validação: O processo já foi realizado antes? Tem dados aqui?
                if (bancoLocal.isNotEmpty() || currentSearch.isNotEmpty() || currentType.isNotEmpty()) {
                    // SIM! Ignora a API completamente e desenha a tela direto do SQLite
                    val convertidos = bancoLocal.map { entity ->
                        Pokemon(
                            id = entity.id, name = entity.name,
                            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${entity.id}.png",
                            types = entity.types.split(","),
                            height = entity.height, weight = entity.weight,
                            stats = if (entity.stats.isBlank()) emptyList() else entity.stats.split(",").map { statStr ->
                                val parts = statStr.split(":")
                                PokemonStat(parts[0], parts[1].toInt())
                            },
                            description = "Sincronização Local." // Aviso que veio do banco
                        )
                    }
                    _pokedex.value = _pokedex.value + convertidos
                    currentOffset += 20

                } else {
                    // 3. NÃO! O banco está vazio (Primeira inicialização ou novo Scroll).
                    // Faz a requisição à rede, obtém os dados e guarda no cache.
                    val novosPokemonsApi = apiClient.getPokemons(limit = 20, offset = currentOffset)
                    if (novosPokemonsApi.isEmpty()) throw Exception("Erro de rede")

                    val cacheEntities = novosPokemonsApi.map {
                        PokemonCacheEntity(
                            id = it.id, name = it.name,
                            types = it.types.joinToString(","),
                            weight = it.weight, height = it.height,
                            stats = it.stats.joinToString(",") { stat -> "${stat.name}:${stat.value}" }
                        )
                    }
                    // Persiste na tabela local
                    pokemonDao.insertAllCache(cacheEntities)

                    _pokedex.value = _pokedex.value + novosPokemonsApi
                    currentOffset += 20
                }

            } catch (e: Exception) {
                // Se der erro de rede logo na primeira inicialização (banco vazio e sem Wi-Fi)
                _uiEvent.emit("Erro ao carregar a Pokédex. Verifique sua internet.")
            }

            isPaginating = false
            _isLoading.value = false
        }
    }

    fun carregarDetalhesPokemon(id: Int) {
        viewModelScope.launch {
            // 1. Limpa o Pokémon anterior para forçar a tela de Loading
            _selectedPokemonDetails.value = null

            try {
                // 2. Bate no endpoint ao vivo via Ktor!
                val pokemonAoVivo = apiClient.getPokemonDetail(id)

                // A SOLUÇÃO: Se o Ktor engolir o erro de rede e devolver nulo, nós forçamos o erro!
                if (pokemonAoVivo == null) {
                    throw Exception("Erro de rede ou Pokémon não encontrado")
                }

                _selectedPokemonDetails.value = pokemonAoVivo
            } catch (e: Exception) {
                // 3. Fallback: Cai aqui automaticamente se estiver offline
                _uiEvent.emit("Sem internet: Usando dados salvos!")

                val fallbackLocal = _pokedex.value.find { it.id == id } ?: _myTeam.value.find { it.id == id }
                _selectedPokemonDetails.value = fallbackLocal
            }
        }
    }

}