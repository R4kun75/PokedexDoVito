package com.example.pokedexkmp.ui

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
import androidx.compose.ui.graphics.StrokeCap
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

// --- IMPORTAÇÕES DA M3 ---
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import dev.icerock.moko.geo.compose.BindLocationTrackerEffect
import dev.icerock.moko.geo.compose.LocationTrackerAccuracy
import dev.icerock.moko.geo.compose.rememberLocationTrackerFactory

// --- IMPORTAÇÕES PARA FORÇAR A PERMISSÃO ---
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import androidx.compose.material.icons.filled.PhotoLibrary


val darkBackground = Color(0xFF121212)

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun PokemonDetailScreen(
    pokemon: Pokemon?,
    isInTeam: Boolean,
    onBackClick: () -> Unit,
    onToggleTeam: (String, Double?, Double?, String?) -> Unit
) {
    if (pokemon == null) {
        Box(modifier = Modifier.fillMaxSize().background(darkBackground), contentAlignment = Alignment.Center) {
            Text("Pokémon não encontrado.", color = Color.White)
        }
        return
    }

    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }


    // --- VARIÁVEIS DE HARDWARE ---
    var photoByteArray by remember { mutableStateOf<ByteArray?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var hardwareError by remember { mutableStateOf<String?>(null) }
    var showCamera by remember { mutableStateOf(false) }

    // 1. Inicializa o Moko Geo
    val locationTrackerFactory = rememberLocationTrackerFactory(accuracy = LocationTrackerAccuracy.Best)
    val locationTracker = remember(locationTrackerFactory) { locationTrackerFactory.createLocationTracker() }
    BindLocationTrackerEffect(locationTracker)

    // 2. Inicializa o Moko Permissions (Garante que a caixinha vai aparecer)
    val permissionsFactory = rememberPermissionsControllerFactory()
    val permissionsController = remember(permissionsFactory) { permissionsFactory.createPermissionsController() }
    BindEffect(permissionsController)

    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = coroutineScope,
        onResult = { byteArrays: List<ByteArray> ->
            val bytes = byteArrays.firstOrNull()

            if (bytes != null) {
                photoByteArray = bytes
                hardwareError = null
            }
        }
    )
    val primaryType = pokemon.types.firstOrNull() ?: "normal"
    val typeGradient = getGradientForType(primaryType)
    val typeColor = getColorForType(primaryType)

    // --- O COMPONENTE DA CÂMERA COM BOTÃO DE CAPTURA ---
    if (showCamera) {
        val cameraState = rememberPeekabooCameraState(onCapture = { bytes ->
            if (bytes != null) {
                photoByteArray = bytes
                hardwareError = null
            }
            showCamera = false // Fecha a câmera automaticamente após tirar a foto
        })

        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            // A Lente da Câmera
            PeekabooCamera(
                state = cameraState,
                modifier = Modifier.fillMaxSize(),
                permissionDeniedContent = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Permissão de câmera negada.", color = Color.White)
                    }
                }
            )

            // Botão de Voltar (caso desista de tirar a foto)
            IconButton(
                onClick = { showCamera = false },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 48.dp, start = 16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }

            // O BOTÃO DE TIRAR A FOTO
            Button(
                onClick = { cameraState.capture() }, // Dispara a ação da foto!
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = CircleShape
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Capturar", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tirar Foto", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
        return // Impede de renderizar o resto da tela
    }

    // --- CORPO DA TELA ORIGINAL (M2) ---
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBackground)
                .verticalScroll(rememberScrollState())
        ) {
            // --- CABEÇALHO ---
            Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
                        .background(typeGradient)
                )

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

                Text(
                    text = pokemon.id.formatPokemonNumber(),
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 48.dp, end = 24.dp)
                )

                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = pokemon.name,
                    modifier = Modifier
                        .size(240.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = 20.dp)
                )
            }

            // --- CORPO E DETALHES ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = pokemon.name.capitalizePokemonName(),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )

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

                Text(
                    text = pokemon.description,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

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
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.Gray.copy(alpha = 0.3f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Altura", color = Color.Gray, fontSize = 12.sp)
                        Text(text = "${pokemon.height / 10f} m", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                        Text(
                            text = stat.name.uppercase(),
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(0.3f)
                        )
                        Text(
                            text = stat.value.toString().padStart(3, '0'),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(0.15f)
                        )
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

                // --- BOTÃO DE AÇÃO ---
                Button(
                    onClick = {
                        if (isInTeam) {
                            onToggleTeam("", null, null, null)
                        } else {
                            showDialog = true
                        }
                    },
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
                        text = if (isInTeam) "Remover da Equipe" else "Capturar (M3)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }


        // --- O POPUP SOBREPOSTO COM HARDWARE ---
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
                        // AQUI TIRAMOS O CAMPO DE TEXTO E COLOCAMOS UMA INSTRUÇÃO
                        Text(
                            text = "Obtenha as coordenadas ou tire uma foto para registrar a captura!",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            IconButton(
                                onClick = { showCamera = true },
                                modifier = Modifier.background(Color(0xFF2C2C2C), CircleShape)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Câmera", tint = Color.White)
                            }

                            // BOTÃO 2: GALERIA
                            IconButton(
                                onClick = { imagePicker.launch() },
                                modifier = Modifier.background(Color(0xFF2C2C2C), CircleShape)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = "Galeria", tint = Color.White)
                            }

                            // BOTÃO 2: GPS
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            hardwareError = "Buscando satélites..."
                                            locationTracker.startTracking()

                                            val loc = locationTracker.getLocationsFlow().first()

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

                        if (hardwareError != null) {
                            Text(text = hardwareError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        }

                        // Exibe as coordenadas na tela assim que buscar com o GPS
                        if (latitude != null && longitude != null) {
                            Text(
                                text = "📍 Lat: ${latitude.toString().take(7)} | Lon: ${longitude.toString().take(7)}",
                                color = Color.Green,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 16.dp)
                            )
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
                            // A MÁGICA AQUI: Salva as coordenadas direto sem campo de texto
                            val finalLocation = if (latitude != null && longitude != null) {
                                "Lat: ${latitude.toString().take(7)}, Lon: ${longitude.toString().take(7)}"
                            } else {
                                "Local Desconhecido"
                            }

                            val base64Image = photoByteArray?.let { Base64.Default.encode(it) }

                            onToggleTeam(finalLocation, latitude, longitude, base64Image)

                            showDialog = false
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