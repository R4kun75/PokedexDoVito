package com.example.pokedexkmp.ui

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCameraPermissionRequester(
    onGranted: () -> Unit,
    onDenied: () -> Unit = {}
): () -> Unit