package com.example.pokedexkmp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberCameraPermissionRequester(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): () -> Unit {

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->

        if (granted) {
            onGranted()
        } else {
            onDenied()
        }

    }

    return remember {

        {

            when {

                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {

                    onGranted()

                }

                else -> {

                    launcher.launch(
                        Manifest.permission.CAMERA
                    )

                }

            }

        }

    }

}