package com.example.pokedexkmp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.pokedexkmp.data.Pokemon

@Composable
fun PokemonDetailScreen(
    pokemon: Pokemon?,
    isInTeam: Boolean,
    onBackClick: () -> Unit,
    onToggleTeam: () -> Unit
) {
    if (pokemon == null) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = Alignment.Center) {
            Text("Pokémon não encontrado.", color = Color.White)
        }
        return
    }

    val primaryType = pokemon.types.firstOrNull()
    val typeGradient = getGradientForType(primaryType)
    val typeColor = getColorForType(primaryType)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground) // Fundo escuro
            .verticalScroll(rememberScrollState())
    ) {
        // --- CABEÇALHO (Gradiente + Imagem + Botão Voltar) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            // Fundo com Gradiente e curva na parte inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
                    .background(typeGradient)
            )

            // Botão de Voltar Funcional e Elegante
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(top = 40.dp, start = 16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }

            // ID do Pokemon no canto superior direito
            Text(
                text = pokemon.id.formatPokemonNumber(),
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 48.dp, end = 24.dp)
            )

            // Imagem do Pokémon flutuando e transbordando o gradiente
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp) // Puxa a imagem para baixo para o efeito 3D
            )
        }

        // --- CORPO DA TELA (Informações e Status) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nome
            Text(
                text = pokemon.name.capitalizePokemonName(),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Tipos
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                pokemon.types.forEach { type ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(getColorForType(type))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = type.capitalizePokemonName(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Descrição (Simulada, já que a API principal não traz)
            Text(
                text = pokemon.description,
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Peso e Altura num card escuro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Peso", color = Color.Gray, fontSize = 12.sp)
                    Text(text = "${pokemon.weight / 10f} kg", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                // Divisor
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.Gray.copy(alpha = 0.3f)))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Altura", color = Color.Gray, fontSize = 12.sp)
                    Text(text = "${pokemon.height / 10f} m", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Status Base (Coloridos de acordo com o tipo!)
            Text(
                text = "Status Base",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            pokemon.stats.forEach { stat ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nome do Status
                    Text(
                        text = stat.name.uppercase(),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.3f)
                    )
                    // Valor do Status
                    Text(
                        text = stat.value.toString().padStart(3, '0'),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.15f)
                    )
                    // Barra de Progresso com a cor do Pokémon
                    LinearProgressIndicator(
                        progress = { (stat.value / 150f).coerceIn(0f, 1f) },
                        modifier = Modifier.weight(0.55f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = typeColor,
                        trackColor = Color(0xFF2C2C2C),
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão de Adicionar ao Time
            Button(
                onClick = onToggleTeam,
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isInTeam) Color(0xFF2C2C2C) else typeColor
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = if (isInTeam) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = if (isInTeam) "Remover da Equipe" else "Adicionar à Equipe",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}