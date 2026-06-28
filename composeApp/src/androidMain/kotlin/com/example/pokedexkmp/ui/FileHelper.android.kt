package com.example.pokedexkmp.ui

import java.io.File
import java.util.UUID

actual fun savePhotoToLocal(bytes: ByteArray): String {
    // Acessa o armazenamento interno seguro do seu aplicativo no Android (não precisa de permissão extra!)
    val path = "/data/data/com.example.pokedexkmp/files"
    val dir = File(path)

    // Se a pasta não existir, cria ela
    if (!dir.exists()) {
        dir.mkdirs()
    }

    // Cria um arquivo físico .png com um nome aleatório único
    val file = File(dir, "pokemon_${UUID.randomUUID()}.png")

    // Salva os bytes que vieram da câmera/galeria direto no arquivo!
    file.writeBytes(bytes)

    // Retorna o "photo_path" exato que o professor pediu no PDF!
    return file.absolutePath
}