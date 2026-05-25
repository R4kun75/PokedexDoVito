package com.example.pokedexkmp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.pokedexkmp.data.Pokemon
import org.jetbrains.compose.resources.painterResource
import pokedexkmp.composeapp.generated.resources.Res
import pokedexkmp.composeapp.generated.resources.compose_multiplatform
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
val DarkBackgroundGrid = Color(0xFF101010)
val PokeRedColor = Color(0xFFE3350D)
@Composable
fun PokedexGridScreen(
    pokemons: List<Pokemon>,
    searchQuery: String,
    selectedType: String, // Novo parâmetro
    onSearchQueryChange: (String) -> Unit,
    onTypeSelect: (String) -> Unit, // Novo parâmetro
    isLoading: Boolean,
    onPokemonClick: (Int) -> Unit,
    onLoadMore: () -> Unit
) {
    // Lista fixa dos tipos principais para exibir nos botões
    val pokemonTypes = listOf("grass", "fire", "water", "bug", "normal", "electric", "poison", "ground")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundGrid)
            .padding(horizontal = 16.dp)
    ) {
        // A sua OutlinedTextField (SearchBar) continua igualzinha aqui...
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            placeholder = { Text("Pesquisar Pokémon...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Pesquisar", tint = Color.Gray) },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedBorderColor = PokeRedColor,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),

            singleLine = true

        )

        // A MÁGICA VISUAL: Botões de Filtro Deslizáveis
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pokemonTypes) { type ->
                val isSelected = selectedType == type
                val backgroundColor = if (isSelected) getColorForType(type) else Color(0xFF1E1E1E)
                val textColor = if (isSelected) Color.White else Color.Gray

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundColor)
                        .clickable { onTypeSelect(type) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = type.capitalizePokemonName(),
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }


        // Se estiver carregando E a lista estiver vazia, mostra o loading no meio
        if (isLoading && pokemons.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PokeRedColor)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pokemons.size) { index ->
                    val pokemon = pokemons[index]

                    // O GATILHO MÁGICO DA PAGINAÇÃO INFINITA
                    if (index >= pokemons.size - 1 && searchQuery.isEmpty()) {
                        LaunchedEffect(Unit) {
                            onLoadMore()
                        }
                    }

                    PokemonGridItem(pokemon = pokemon, onClick = { onPokemonClick(pokemon.id) })
                }
            }
        }
    }
} // <- ESTA É A CHAVE QUE FECHA A TELA PRINCIPAL

// A PARTIR DAQUI, O COMPONENTE FICA ISOLADO CORRETAMENTE
@Composable
private fun PokemonGridItem(pokemon: Pokemon, onClick: () -> Unit) {
    val primaryType = pokemon.types.firstOrNull()
    val cardGradient = getGradientForType(primaryType)

    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardGradient)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = null,
                alpha = 0.1f,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(120.dp).align(Alignment.BottomEnd).offset(x = 20.dp, y = 20.dp)
            )

            Column(modifier = Modifier.align(Alignment.TopStart)) {
                Text(
                    text = pokemon.name.capitalizePokemonName(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                pokemon.types.forEach { type ->
                    Box(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = type.capitalizePokemonName(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = 8.dp)
            )
        }
    }
}