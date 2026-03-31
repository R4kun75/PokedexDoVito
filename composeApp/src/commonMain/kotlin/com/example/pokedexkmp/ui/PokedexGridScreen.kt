package com.example.pokedexkmp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import pokedexkmp.composeapp.generated.resources.compose_multiplatform // Placeholder

// Importamos a cor de fundo escura que definimos na WelcomeScreen
val DarkBackgroundGrid = Color(0xFF101010)

@Composable
fun PokedexGridScreen(
    pokemons: List<Pokemon>,
    isLoading: Boolean,
    onPokemonClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundGrid) // Fundo Escuro para Imersão
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Pokedex",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            // Ícone de Menu estilizado (Placeholder)
            Icon(
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "Encontre seus Pokémon favoritos!",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PokeRed) // Usando a cor vermelha do tema
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pokemons) { pokemon ->
                    PokemonGridItem(pokemon = pokemon, onClick = { onPokemonClick(pokemon.id) })
                }
            }
        }
    }
}

@Composable
private fun PokemonGridItem(pokemon: Pokemon, onClick: () -> Unit) {
    // Pega o tipo primário para definir a afinidade de cor
    val primaryType = pokemon.types.firstOrNull()
    val cardGradient = getGradientForType(primaryType)

    // Layout customizado, sem usar o Card padrão do Material para controle total
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(24.dp)) // Cantos Arredondados Profundos
            .background(cardGradient) // Gradiente Vibrante de Fundo
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Padrão de Pokebola no fundo (Placeholder estilizado)
        Image(
            painter = painterResource(Res.drawable.compose_multiplatform),
            contentDescription = null,
            alpha = 0.1f,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier.size(120.dp).align(Alignment.BottomEnd).offset(x = 20.dp, y = 20.dp)
        )

        // Conteúdo de Texto (Esquerda)
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Text(
                text = pokemon.name.capitalizePokemonName(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Chips de tipo estilizados em branco translúcido
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

        // Imagem do Pokémon (Direita, sobrepondo o fundo)
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