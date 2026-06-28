package com.example.pokedexkmp.ui

import androidx.compose.runtime.Composable

@Composable
actual fun rememberCameraPermissionRequester(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): () -> Unit {

    return {

        onGranted()

    }

}