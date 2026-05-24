package com.example.pokedexkmp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {

    // ========== REQUISITOS DA POKÉDEX (OFFLINE-FIRST & PAGINAÇÃO) ==========

    // 1. Salva a lista inicial vinda da API no banco de dados
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCache(pokemons: List<PokemonCacheEntity>)

    // 2. Consulta paginada com filtro de busca (SearchBar) direto no SQL
    @Query("""
        SELECT * FROM pokemon_cache 
        WHERE name LIKE :searchQuery 
        ORDER BY id ASC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getPokedexPage(limit: Int, offset: Int, searchQuery: String): List<PokemonCacheEntity>

    // 3. Verifica se o cache já possui registros para evitar downloads redundantes
    @Query("SELECT COUNT(*) FROM pokemon_cache")
    suspend fun getCacheCount(): Int


    // ========== REQUISITOS DO TIME / FAVORITOS (REGRA DE NEGÓCIO) ==========

    // 4. Adiciona um Pokémon ao time salvando obrigatoriamente o local de captura
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMember(pokemon: PokemonTeamEntity)

    // 5. Lista todos os Pokémons do seu time salvos localmente
    @Query("SELECT * FROM pokemon_team ORDER BY id ASC")
    suspend fun getTeam(): List<PokemonTeamEntity>

    // 6. Remove um Pokémon do time
    @Delete
    suspend fun removeTeamMember(pokemon: PokemonTeamEntity)

    // 7. Verifica se um Pokémon específico já está no time (útil para mudar o ícone do botão)
    @Query("SELECT EXISTS(SELECT 1 FROM pokemon_team WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean
}