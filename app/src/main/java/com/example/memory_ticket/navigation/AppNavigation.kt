package com.example.memory_ticket.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.memory_ticket.firebase.FirebaseManager
import com.example.memory_ticket.ui.screens.auth.LoginScreen
import com.example.memory_ticket.ui.screens.auth.RegisterScreen
import com.example.memory_ticket.ui.screens.home.HomeScreen
import com.example.memory_ticket.viewmodel.AuthViewModel
import com.example.memory_ticket.ui.screens.create.CreateMemoryScreen
private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"

    const val CREATE_MEMORY = "create_memory"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

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
                    navController.navigate(Routes.CREATE_MEMORY)
                }
            )
        }

        composable(Routes.CREATE_MEMORY) {
            CreateMemoryScreen(
                onBack = {
                    navController.popBackStack()
                },
                onTakePhoto = {
                    // CameraX will be connected next
                },
                onChooseFromGallery = {
                    // Gallery will be connected next
                }
            )
        }
    }
}