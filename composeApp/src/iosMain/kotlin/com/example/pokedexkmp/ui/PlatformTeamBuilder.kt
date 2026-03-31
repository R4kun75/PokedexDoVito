// composeApp/src/iosMain/kotlin/com/example/pokedexkmp/ui/PlatformTeamBuilder.kt
package com.example.pokedexkmp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
    // Cores nativas do iOS (System Background e System Gray)
    val iosBackground = Color(0xFFF2F2F7)
    val iosRed = Color(0xFFFF3B30)
    val iosSeparator = Color(0xFFC6C6C8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(iosBackground)
    ) {
        // Imitando o Large Title do iOS
        Text(
            text = "Meu Time",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, top = 48.dp, bottom = 16.dp)
        )

        if (myTeam.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Você não selecionou nenhum Pokémon :(",
                    color = Color.Gray,
                    fontSize = 17.sp
                )
            }
        } else {
            // Bloco de lista estilo iOS Settings
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
            ) {
                itemsIndexed(myTeam) { index, pokemon ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = pokemon.imageUrl,
                                contentDescription = pokemon.name,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = pokemon.name.capitalizePokemonName(),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                        IconButton(onClick = { onRemoveClick(pokemon) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remover",
                                tint = iosRed
                            )
                        }
                    }
                    // Linha separadora clássica do iOS (não aparece no último item)
                    if (index < myTeam.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 84.dp),
                            color = iosSeparator,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}