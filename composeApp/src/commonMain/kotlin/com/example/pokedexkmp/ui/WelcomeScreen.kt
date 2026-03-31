package com.example.pokedexkmp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import pokedexkmp.composeapp.generated.resources.Res
import pokedexkmp.composeapp.generated.resources.compose_multiplatform // Use uma logo temporária se não tiver a do Gengar

// Cores inspiradas na imagem de referência
val DarkBackground = Color(0xFF101010)
val PokeRed = Color(0xFFE3350D)

@Composable
fun WelcomeScreen(onGetStartedClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Centralizada (Substitua pela imagem correta se tiver o asset)
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(Color(0xFF1A1A1A), CircleShape), // Círculo escuro de fundo da logo
            contentAlignment = Alignment.Center
        ) {
            // Usando a logo do Compose como placeholder. O ideal é o asset da imagem 4.
            Image(
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = "Pokedex Logo",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Texto de Título
        Text(
            text = "Pokédex",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtítulo
        Text(
            text = "Todos os Pokémon em um só lugar. Monte seu time e torne-se um mestre!",
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Empurra o botão para baixo

        // Botão "Get Started" Vermelho
        Button(
            onClick = onGetStartedClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PokeRed),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                text = "Vamos Começar",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}