package com.example.pokedexkmp.ui

import kotlinx.coroutines.flow.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.pokedexkmp.data.Pokemon
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

// --- IMPORTAÇÕES CORRIGIDAS DA M3 ---
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import dev.icerock.moko.geo.compose.BindLocationTrackerEffect
import dev.icerock.moko.geo.compose.LocationTrackerAccuracy
import dev.icerock.moko.geo.compose.rememberLocationTrackerFactory

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun PokemonDetailScreen(
    pokemon: Pokemon?,
    isInTeam: Boolean,
    onBackClick: () -> Unit,
    onToggleTeam: (String, Double?, Double?, String?) -> Unit
) {
    if (pokemon == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)), contentAlignment = Alignment.Center) {
            Text("Pokémon não encontrado.", color = Color.White)
        }
        return
    }

    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var locationInput by remember { mutableStateOf("") }

    // --- VARIÁVEIS DE HARDWARE ---
    var photoByteArray by remember { mutableStateOf<ByteArray?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var hardwareError by remember { mutableStateOf<String?>(null) }

    // Controle da Tela Cheia da Câmera
    var showCamera by remember { mutableStateOf(false) }

    // 1. Inicializa o Moko Geo (Usando a precisão padrão automática!)
    val locationTrackerFactory = rememberLocationTrackerFactory(accuracy = LocationTrackerAccuracy.Best)
    val locationTracker = remember(locationTrackerFactory) { locationTrackerFactory.createLocationTracker() }
    BindLocationTrackerEffect(locationTracker)

    // Cores de Tipagem (Mockadas para simplificar o exemplo)
    val typeColor = Color(0xFFE3350D)

    // --- O COMPONENTE DA CÂMERA EM TELA CHEIA ---
    if (showCamera) {
        val cameraState = rememberPeekabooCameraState(onCapture = { bytes ->
            if (bytes != null) {
                photoByteArray = bytes
                hardwareError = null
            }
            showCamera = false // Fecha a câmera após tirar a foto
        })

        PeekabooCamera(
            state = cameraState,
            modifier = Modifier.fillMaxSize(),
            permissionDeniedContent = {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Permissão de câmera negada pelo usuário.", color = Color.White)
                        Button(onClick = { showCamera = false }, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Voltar")
                        }
                    }
                }
            }
        )
        // O return impede que o restante da tela desenhe por trás da câmera
        return
    }

    // --- CORPO NORMAL DA TELA ---
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .verticalScroll(rememberScrollState())
        ) {

            // ... (AQUI FICA A IMAGEM DO POKEMON, NOME, STATS - MANTENHA O SEU CÓDIGO) ...
            Spacer(modifier = Modifier.height(300.dp))

            Button(
                onClick = {
                    if (isInTeam) {
                        onToggleTeam("", null, null, null)
                    } else {
                        showDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isInTeam) Color(0xFF2C2C2C) else typeColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = if (isInTeam) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = if (isInTeam) "Remover da Equipe" else "Capturar (M3)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // --- POPUP COM OS BOTÕES DE HARDWARE ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                containerColor = Color(0xFF1E1E1E),
                titleContentColor = Color.White,
                textContentColor = Color.White,
                title = { Text(text = "Registro de Captura") },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = locationInput,
                            onValueChange = { locationInput = it },
                            placeholder = { Text("Nome da Cidade ou Rota", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // BOTÃO DA CÂMERA: Apenas muda o estado para exibir a tela
                            IconButton(
                                onClick = { showCamera = true },
                                modifier = Modifier.background(Color(0xFF2C2C2C), CircleShape)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Câmera", tint = Color.White)
                            }

                            // BOTÃO DO GPS
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            hardwareError = "Buscando satélites..."
                                            locationTracker.startTracking()

                                            val loc = locationTracker.getLocationsFlow().first()
                                            // A mágica acontece aqui: agora a IDE reconhece o núcleo!
                                            latitude = loc.latitude
                                            longitude = loc.longitude

                                            locationTracker.stopTracking()
                                            hardwareError = null
                                        } catch (e: Exception) {
                                            hardwareError = "Erro no GPS ou Permissão negada."
                                        }
                                    }
                                },
                                modifier = Modifier.background(Color(0xFF2C2C2C), CircleShape)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = "GPS", tint = Color.White)
                            }
                        }

                        // Feedbacks Visuais
                        if (hardwareError != null) {
                            Text(text = hardwareError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        }
                        if (latitude != null) {
                            Text(text = "📍 Lat: ${latitude.toString().take(7)} | Lon: ${longitude.toString().take(7)}",
                                color = Color.Green, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        }
                        if (photoByteArray != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            AsyncImage(
                                model = photoByteArray,
                                contentDescription = "Sua Foto",
                                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val finalLocation = if (locationInput.isBlank()) "Local Desconhecido" else locationInput
                            val base64Image = photoByteArray?.let { Base64.Default.encode(it) }

                            onToggleTeam(finalLocation, latitude, longitude, base64Image)

                            showDialog = false
                            locationInput = ""
                            photoByteArray = null
                            latitude = null
                            longitude = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = typeColor)
                    ) {
                        Text("Salvar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }
    }
}