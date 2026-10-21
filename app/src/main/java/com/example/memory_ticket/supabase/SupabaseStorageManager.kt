package com.example.memory_ticket.supabase

import android.content.Context
import android.net.Uri
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class SupabaseStorageManager(
    private val context: Context
) {

    private val client = HttpClient(Android)

    suspend fun uploadImage(
        userId: String,
        imageUri: Uri
    ): String = withContext(Dispatchers.IO) {

        val mimeType =
            context.contentResolver.getType(imageUri)
                ?: "image/jpeg"

        val extension = when (mimeType.lowercase()) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/heic" -> "heic"
            "image/heif" -> "heif"
            else -> "jpg"
        }

        val fileName =
            "${UUID.randomUUID()}.$extension"

        val path =
            "$userId/$fileName"

        val imageBytes =
            context.contentResolver
                .openInputStream(imageUri)
                ?.use { inputStream ->
                    inputStream.readBytes()
                }
                ?: throw IllegalStateException(
                    "Unable to read selected image."
                )

        val baseUrl = SupabaseConfig.projectUrl.trimEnd('/')

        val uploadUrl =
            "$baseUrl/storage/v1/object/${SupabaseConfig.BUCKET_NAME}/$path"

        val contentType =
            ContentType.parse(mimeType)

        val response = client.post {
            url(uploadUrl)

            header(
                "apikey",
                SupabaseConfig.publishableKey
            )

            header(
                HttpHeaders.Authorization,
                "Bearer ${SupabaseConfig.publishableKey}"
            )

            header(
                HttpHeaders.ContentType,
                contentType.toString()
            )

            header(
                "x-upsert",
                "false"
            )

            setBody(imageBytes)
        }

        if (
            response.status != HttpStatusCode.OK &&
            response.status != HttpStatusCode.Created
        ) {
            throw IllegalStateException(
                "Supabase upload failed: " +
                        "${response.status} " +
                        response.bodyAsText()
            )
        }

        "$baseUrl/storage/v1/object/public/${SupabaseConfig.BUCKET_NAME}/$path"
    }

    fun close() {
        client.close()
    }
}