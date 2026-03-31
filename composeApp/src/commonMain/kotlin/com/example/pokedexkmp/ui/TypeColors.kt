// composeApp/src/commonMain/kotlin/com/example/pokedexkmp/ui/TypeColors.kt
package com.example.pokedexkmp.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Paleta de Cores Vibrantes para o Design Moderno
object PokeTypeColors {
    val Grass = Color(0xFF49D0B0)
    val GrassDark = Color(0xFF19A080)

    val Fire = Color(0xFFFB6C6C)
    val FireDark = Color(0xFFCB3C3C)

    val Water = Color(0xFF76BDFE)
    val WaterDark = Color(0xFF468DCE)

    val Bug = Color(0xFF67BC5C)
    val Normal = Color(0xFFACA98A)
    val Electric = Color(0xFFFFCE4B)
    val Fairy = Color(0xFFEA1369)
    val Ground = Color(0xFFE2C56A)
    val Psychic = Color(0xFFFF5AB3)
    val Rock = Color(0xFFB6A136)
    val Ghost = Color(0xFF735797)
    val Ice = Color(0xFF96D9D6)
    val Dragon = Color(0xFF6F35FC)
    val Poison = Color(0xFFA33EA1)
    val Steel = Color(0xFFB7B7CE)
    val Dark = Color(0xFF705746)
    val Flying = Color(0xFFA98FF0)
    val Fighting = Color(0xFFC22E28)

    val Default = Color(0xFFBDBDBD)
}

// Retorna uma cor sólida baseada no tipo primário
fun getColorForType(type: String?): Color {
    return when (type?.lowercase()) {
        "grass" -> PokeTypeColors.Grass
        "fire" -> PokeTypeColors.Fire
        "water" -> PokeTypeColors.Water
        "bug" -> PokeTypeColors.Bug
        "electric" -> PokeTypeColors.Electric
        "poison" -> PokeTypeColors.Poison
        "fairy" -> PokeTypeColors.Fairy
        "normal" -> PokeTypeColors.Normal
        "psychic" -> PokeTypeColors.Psychic
        "ground" -> PokeTypeColors.Ground
        "rock" -> PokeTypeColors.Rock
        "ghost" -> PokeTypeColors.Ghost
        "ice" -> PokeTypeColors.Ice
        "dragon" -> PokeTypeColors.Dragon
        "steel" -> PokeTypeColors.Steel
        "dark" -> PokeTypeColors.Dark
        "flying" -> PokeTypeColors.Flying
        "fighting" -> PokeTypeColors.Fighting
        else -> PokeTypeColors.Default
    }
}

// Retorna um Gradiente Vibrante para o fundo dos Cards (Suco de Design!)
fun getGradientForType(type: String?): Brush {
    val startColor = getColorForType(type)
    val endColor = when (type?.lowercase()) {
        "grass" -> PokeTypeColors.GrassDark
        "fire" -> PokeTypeColors.FireDark
        "water" -> PokeTypeColors.WaterDark
        else -> startColor.copy(alpha = 0.8f) // Escurece um pouco para os outros
    }

    return Brush.linearGradient(
        colors = listOf(startColor, endColor)
    )
}