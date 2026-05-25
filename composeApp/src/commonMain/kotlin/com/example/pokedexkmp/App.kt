package com.example.pokedexkmp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pokedexkmp.data.local.AppDatabase
import com.example.pokedexkmp.navigation.*
import com.example.pokedexkmp.ui.*

val DarkSurface = Color(0xFF1E1E1E)
val DarkBackground = Color(0xFF101010)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(database: AppDatabase) { // <-- RECEBENDO O BANCO AQUI!
    MaterialTheme(colorScheme = darkColorScheme(background = DarkBackground, surface = DarkSurface)) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // Passando o banco para o ViewModel
        val viewModel = viewModel { PokedexViewModel(database) }
        val pokedex by viewModel.filteredPokedex.collectAsState()
        val searchQuery by viewModel.searchQuery.collectAsState()
        val myTeam by viewModel.myTeam.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val selectedType by viewModel.selectedType.collectAsState() // Novo Estado

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.uiEvent.collect { message ->
                snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
            }
        }

        val showBottomBar = currentDestination?.hierarchy?.any {
            it.hasRoute(PokedexRoute::class) || it.hasRoute(TeamBuilderRoute::class)
        } == true

        val topBarTitle = when {
            currentDestination?.hierarchy?.any { it.hasRoute(PokedexRoute::class) } == true -> "Pokédex"
            currentDestination?.hierarchy?.any { it.hasRoute(TeamBuilderRoute::class) } == true -> "Meu Time"
            else -> ""
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(), // Garante que não encolhe para tela branca
            containerColor = DarkBackground,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (topBarTitle.isNotEmpty()) {
                    TopAppBar(
                        title = { Text(topBarTitle, fontWeight = FontWeight.Bold) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = DarkBackground,
                            titleContentColor = Color.White
                        )
                    )
                }
            },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(containerColor = DarkSurface, contentColor = Color.White) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.List, contentDescription = "Pokédex") },
                            label = { Text("Pokédex") },
                            selected = currentDestination?.hierarchy?.any { it.hasRoute(PokedexRoute::class) } == true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PokeRed, selectedTextColor = PokeRed,
                                unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFF2C2C2C)
                            ),
                            onClick = {
                                navController.navigate(PokedexRoute) {
                                    popUpTo(WelcomeRoute) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Favorite, contentDescription = "Meu Time") },
                            label = { Text("Meu Time") },
                            selected = currentDestination?.hierarchy?.any { it.hasRoute(TeamBuilderRoute::class) } == true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PokeRed, selectedTextColor = PokeRed,
                                unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFF2C2C2C)
                            ),
                            onClick = {
                                navController.navigate(TeamBuilderRoute) {
                                    popUpTo(WelcomeRoute) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = WelcomeRoute,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                composable<WelcomeRoute> {
                    WelcomeScreen(onGetStartedClick = {
                        navController.navigate(PokedexRoute) { popUpTo(WelcomeRoute) { inclusive = true } }
                    })
                }

                composable<PokedexRoute> {
                    PokedexGridScreen(
                        pokemons = pokedex,
                        searchQuery = searchQuery,
                        selectedType = selectedType, // <--- PASSA PARA A TELA
                        isLoading = isLoading,
                        onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                        onTypeSelect = { viewModel.onTypeSelected(it) }, // <--- CLIQUE DO FILTRO
                        onPokemonClick = { pokemonId -> navController.navigate(PokemonDetailRoute(pokemonId)) },
                        onLoadMore = { viewModel.carregarMaisPokemons() }
                    )
                }

                composable<PokemonDetailRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<PokemonDetailRoute>()
                    val selectedPokemon = pokedex.find { it.id == route.pokemonId } ?: myTeam.find { it.id == route.pokemonId }

                    if (selectedPokemon != null) {
                        val isPokemonInTeam = myTeam.any { it.id == selectedPokemon.id }

                        PokemonDetailScreen(
                            pokemon = selectedPokemon,
                            isInTeam = isPokemonInTeam,
                            onBackClick = { navController.popBackStack() },
                            onToggleTeam = { localDigitado ->
                                if (isPokemonInTeam) {
                                    viewModel.removeFromTeam(selectedPokemon)
                                } else {
                                    viewModel.addToTeam(selectedPokemon, localDigitado)
                                }
                            }
                        )
                    }
                }

                composable<TeamBuilderRoute> {
                    TeamBuilderScreen(
                        myTeam = myTeam,
                        onRemoveClick = { pokemon -> viewModel.removeFromTeam(pokemon) }
                    )
                }
            }
        }
    }
}