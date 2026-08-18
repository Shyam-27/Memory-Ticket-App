package com.example.memory_ticket.viewmodel

import androidx.lifecycle.ViewModel
import com.example.memory_ticket.firebase.FirebaseManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.FieldValue

class AuthViewModel : ViewModel() {

    private val auth = FirebaseManager.auth

    private val _currentUser =
        MutableStateFlow<FirebaseUser?>(auth.currentUser)

    val currentUser: StateFlow<FirebaseUser?> =
        _currentUser.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    private val _isSuccess =
        MutableStateFlow(false)

    val isSuccess: StateFlow<Boolean> =
        _isSuccess.asStateFlow()

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {

        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match."
            return
        }

        if (password.length < 6) {
            _errorMessage.value =
                "Password must contain at least 6 characters."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        _isSuccess.value = false

        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->

            if (!task.isSuccessful) {
                _isLoading.value = false

                _errorMessage.value =
                    task.exception?.localizedMessage
                        ?: "Registration failed."

                return@addOnCompleteListener
            }

            val user = auth.currentUser

            if (user == null) {
                _isLoading.value = false
                _errorMessage.value = "User account was created, but user data is unavailable."
                return@addOnCompleteListener
            }

            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "createdAt" to FieldValue.serverTimestamp()
            )

            FirebaseManager.firestore
                .collection("users")
                .document(user.uid)
                .set(userData)
                .addOnCompleteListener { firestoreTask ->

                    _isLoading.value = false

                    if (firestoreTask.isSuccessful) {
                        _currentUser.value = user
                        _isSuccess.value = true
                    } else {
                        _errorMessage.value =
                            firestoreTask.exception?.localizedMessage
                                ?: "Account created, but profile could not be saved."
                    }
                }
        }
    }

    fun login(
        email: String,
        password: String
    ) {

        _isLoading.value = true
        _errorMessage.value = null
        _isSuccess.value = false

        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->

            _isLoading.value = false

            if (task.isSuccessful) {

                _currentUser.value = auth.currentUser
                _isSuccess.value = true

            } else {

                _errorMessage.value =
                    task.exception?.localizedMessage
                        ?: "Login failed."
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _isSuccess.value = false
    }
}