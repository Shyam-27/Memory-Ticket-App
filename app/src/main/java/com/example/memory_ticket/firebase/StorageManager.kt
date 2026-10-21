package com.example.memory_ticket.firebase

import android.net.Uri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class StorageManager {

    private val storage = FirebaseManager.storage

    suspend fun uploadMemoryImage(
        userId: String,
        memoryId: String,
        imageUri: Uri
    ): String {

        val imageRef: StorageReference =
            storage.reference
                .child("memory-images")
                .child(userId)
                .child("$memoryId.jpg")

        imageRef.putFile(imageUri).await()

        return imageRef.downloadUrl.await().toString()
    }
}