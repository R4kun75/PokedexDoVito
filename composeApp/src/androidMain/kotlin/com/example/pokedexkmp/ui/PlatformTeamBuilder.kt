package com.example.pokedexkmp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.pokedexkmp.data.Pokemon

@Composable
actual fun TeamBuilderScreen(myTeam: List<Pokemon>, onRemoveClick: (Pokemon) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundGrid) // Mantém o tema escuro
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp, top = 12.dp)) {
            Text(
                text = "Minha Equipe",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(12.dp))
            // Contador estilizado (Suco de Detalhe!)
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PokeRed)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${myTeam.size}/6",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        if (myTeam.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Adicione Pokémons à sua equipe na tela de detalhes!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(myTeam) { pokemon ->
                    TeamMemberCard(pokemon = pokemon, onRemoveClick = { onRemoveClick(pokemon) })
                }
            }
        }
    }
}

@Composable
private fun TeamMemberCard(pokemon: Pokemon, onRemoveClick: () -> Unit) {
    val primaryType = pokemon.types.firstOrNull()
    // Herda o gradiente vibrante da Grid para consistência visual
    val cardGradient = getGradientForType(primaryType)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(cardGradient) // Gradiente Dinâmico por afinidade
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagem Centralizada num fundo circular translúcido
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(65.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info (Nome e ID)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = pokemon.name.capitalizePokemonName(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = pokemon.id.formatPokemonNumber(),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Row(modifier = Modifier.padding(top = 8.dp)) {
                pokemon.types.forEach { type ->
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = type.capitalizePokemonName(),
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Botão de Remover Customizado (Sutil e elegante)
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.1f))
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remover",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}