package com.example.memory_ticket.supabase

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.memory_ticket.firebase.FirebaseManager
import kotlinx.coroutines.launch

@Composable
fun SupabaseUploadTestScreen() {

    val context = androidx.compose.ui.platform.LocalContext.current

    val scope = rememberCoroutineScope()

    var selectedUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var message by remember {
        mutableStateOf("Select a photo to test Supabase.")
    }

    var isUploading by remember {
        mutableStateOf(false)
    }

    val picker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->

            if (uri != null) {
                selectedUri = uri
                message = "Photo selected. Ready to upload."
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(message)

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {

                picker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose Photo")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = {

                val uri = selectedUri

                if (uri == null) {
                    message = "Please select a photo first."
                    return@Button
                }

                val user =
                    FirebaseManager.auth.currentUser

                if (user == null) {
                    message = "Please log in first."
                    return@Button
                }

                scope.launch {

                    try {

                        isUploading = true
                        message = "Uploading..."

                        val manager =
                            SupabaseStorageManager(context)

                        val imageUrl =
                            manager.uploadImage(
                                userId = user.uid,
                                imageUri = uri
                            )

                        manager.close()

                        isUploading = false

                        message =
                            "UPLOAD SUCCESS!\n\n$imageUrl"

                    } catch (exception: Exception) {

                        isUploading = false

                        message =
                            "UPLOAD FAILED:\n" +
                                    (
                                            exception.localizedMessage
                                                ?: "Unknown error"
                                            )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedUri != null && !isUploading
        ) {

            if (isUploading) {
                CircularProgressIndicator()
            } else {
                Text("Upload to Supabase")
            }
        }
    }
}