package com.example.pokedexkmp.ui

import androidx.compose.runtime.Composable
import com.example.pokedexkmp.data.Pokemon

// Aqui criamos a "promessa" da interface. A implementação real será feita nas pastas específicas de cada OS.
@Composable
expect fun TeamBuilderScreen(myTeam: List<Pokemon>, onRemoveClick: (Pokemon) -> Unit)