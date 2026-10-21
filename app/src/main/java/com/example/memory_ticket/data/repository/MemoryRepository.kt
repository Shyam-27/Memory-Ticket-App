package com.example.memory_ticket.data.repository

import com.example.memory_ticket.data.model.Memory
import com.example.memory_ticket.firebase.FirebaseManager
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MemoryRepository {

    private val firestore = FirebaseManager.firestore

    suspend fun saveMemory(
        userId: String,
        title: String,
        category: String,
        description: String,
        ticketStyle: String,
        imageUrl: String
    ): Result<String> {
        return try {
            val memoryId = UUID.randomUUID().toString()

            val memory = hashMapOf(
                "memoryId" to memoryId,
                "userId" to userId,
                "title" to title,
                "category" to category,
                "description" to description,
                "imageUrl" to imageUrl,
                "ticketStyle" to ticketStyle,
                "createdAt" to System.currentTimeMillis()
            )

            firestore
                .collection("memories")
                .document(memoryId)
                .set(memory)
                .await()

            Result.success(memoryId)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMemories(userId: String): Result<List<Memory>> {
        return try {
            val snapshot = firestore
                .collection("memories")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val list = snapshot.toObjects(Memory::class.java)
                .sortedByDescending { it.createdAt }

            Result.success(list)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
