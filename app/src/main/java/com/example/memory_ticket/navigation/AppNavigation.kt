package com.example.memory_ticket.navigation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.memory_ticket.data.repository.MemoryRepository
import com.example.memory_ticket.firebase.FirebaseManager
import com.example.memory_ticket.supabase.SupabaseStorageManager
import com.example.memory_ticket.ui.screens.auth.LoginScreen
import com.example.memory_ticket.ui.screens.auth.RegisterScreen
import com.example.memory_ticket.ui.screens.create.CameraScreen
import com.example.memory_ticket.ui.screens.create.CreateMemoryScreen
import com.example.memory_ticket.ui.screens.create.CustomizeTicketScreen
import com.example.memory_ticket.ui.screens.home.HomeScreen
import com.example.memory_ticket.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CREATE_MEMORY = "create_memory"
    const val CAMERA = "camera"
    const val CUSTOMIZE_TICKET = "customize_ticket"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()

    val coroutineScope = rememberCoroutineScope()

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val startDestination =
        if (FirebaseManager.auth.currentUser != null) {
            Routes.HOME
        } else {
            Routes.LOGIN
        }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ---------------- LOGIN ----------------

        composable(Routes.LOGIN) {

            LoginScreen(
                authViewModel = authViewModel,

                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                },

                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        // ---------------- REGISTER ----------------

        composable(Routes.REGISTER) {

            RegisterScreen(
                authViewModel = authViewModel,

                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.REGISTER) {
                            inclusive = true
                        }
                    }
                },

                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // ---------------- HOME ----------------

        composable(Routes.HOME) {

            HomeScreen(
                onLogout = {

                    authViewModel.logout()

                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                    }
                },

                onAddMemory = {

                    selectedImageUri = null

                    navController.navigate(
                        Routes.CREATE_MEMORY
                    )
                }
            )
        }

        // ---------------- CREATE MEMORY ----------------

        composable(Routes.CREATE_MEMORY) {

            CreateMemoryScreen(
                onBack = {
                    navController.popBackStack()
                },

                onTakePhoto = {
                    navController.navigate(Routes.CAMERA)
                },

                onPhotoSelected = { uri ->

                    selectedImageUri = uri

                    navController.navigate(
                        Routes.CUSTOMIZE_TICKET
                    )
                }
            )
        }

        // ---------------- CAMERA ----------------

        composable(Routes.CAMERA) {

            CameraScreen(
                onPhotoCaptured = { uri ->

                    selectedImageUri = uri

                    navController.navigate(Routes.CUSTOMIZE_TICKET) {
                        popUpTo(Routes.CAMERA) {
                            inclusive = true
                        }
                    }
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        //---------------- CUSTOMIZE TICKET ----------------

        composable(Routes.CUSTOMIZE_TICKET) {

            val imageUri = remember { selectedImageUri }

            if (imageUri == null) {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
                return@composable
            }

            var isSaving by remember {
                mutableStateOf(false)
            }

            BackHandler(enabled = isSaving) {
                // Prevent back navigation while save/upload is in progress
            }

            val context = LocalContext.current

            CustomizeTicketScreen(
                imageUri = imageUri,
                isSaving = isSaving,

                onSave = { title,
                            category,
                            description,
                            ticketStyle ->

                    if (isSaving) return@CustomizeTicketScreen

                    val currentUser = FirebaseManager.auth.currentUser

                    if (currentUser != null) {

                        isSaving = true

                        coroutineScope.launch {

                            val storageManager =
                                SupabaseStorageManager(context)

                            try {

                                // 1. Upload photo to Supabase
                                val imageUrl =
                                    storageManager.uploadImage(
                                        userId = currentUser.uid,
                                        imageUri = imageUri
                                    )

                                // 2. Save memory ticket to Firestore
                                val repository = MemoryRepository()
                                val saveResult = repository.saveMemory(
                                    userId = currentUser.uid,
                                    title = title,
                                    category = category,
                                    description = description,
                                    ticketStyle = ticketStyle,
                                    imageUrl = imageUrl
                                )

                                if (saveResult.isFailure) {
                                    throw saveResult.exceptionOrNull()
                                        ?: Exception("Failed to save memory details.")
                                }

                                Toast.makeText(
                                    context,
                                    "Memory ticket saved successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                selectedImageUri = null

                                navController.navigate(
                                    Routes.HOME
                                ) {
                                    popUpTo(Routes.HOME) {
                                        inclusive = true
                                    }
                                }

                            } catch (e: Exception) {

                                Toast.makeText(
                                    context,
                                    "Save failed: ${e.localizedMessage ?: e.message}",
                                    Toast.LENGTH_LONG
                                ).show()

                            } finally {

                                storageManager.close()
                                isSaving = false
                            }
                        }

                    } else {

                        Toast.makeText(
                            context,
                            "Please login again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },

                onBack = {
                    if (!isSaving) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
