package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AuthRepository
import com.example.model.UserAccount
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<UserAccount?> = authRepository.currentUser
    val isAuthenticated: StateFlow<Boolean> = authRepository.isAuthenticated

    // Login Form
    private val _loginEmail = MutableStateFlow("arun@example.com")
    val loginEmail: StateFlow<String> = _loginEmail.asStateFlow()

    private val _loginPassword = MutableStateFlow("password123")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    // Sign Up Form
    private val _signUpName = MutableStateFlow("")
    val signUpName: StateFlow<String> = _signUpName.asStateFlow()

    private val _signUpEmail = MutableStateFlow("")
    val signUpEmail: StateFlow<String> = _signUpEmail.asStateFlow()

    private val _signUpPassword = MutableStateFlow("")
    val signUpPassword: StateFlow<String> = _signUpPassword.asStateFlow()

    private val _signUpConfirmPassword = MutableStateFlow("")
    val signUpConfirmPassword: StateFlow<String> = _signUpConfirmPassword.asStateFlow()

    // Forgot Password Form
    private val _forgotPasswordEmail = MutableStateFlow("")
    val forgotPasswordEmail: StateFlow<String> = _forgotPasswordEmail.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _eventMessage = MutableSharedFlow<String>()
    val eventMessage: SharedFlow<String> = _eventMessage.asSharedFlow()

    fun onLoginEmailChange(value: String) {
        _loginEmail.value = value
        _errorMessage.value = null
    }

    fun onLoginPasswordChange(value: String) {
        _loginPassword.value = value
        _errorMessage.value = null
    }

    fun onSignUpNameChange(value: String) {
        _signUpName.value = value
        _errorMessage.value = null
    }

    fun onSignUpEmailChange(value: String) {
        _signUpEmail.value = value
        _errorMessage.value = null
    }

    fun onSignUpPasswordChange(value: String) {
        _signUpPassword.value = value
        _errorMessage.value = null
    }

    fun onSignUpConfirmPasswordChange(value: String) {
        _signUpConfirmPassword.value = value
        _errorMessage.value = null
    }

    fun onForgotPasswordEmailChange(value: String) {
        _forgotPasswordEmail.value = value
        _errorMessage.value = null
    }

    fun login(onSuccess: () -> Unit) {
        if (_loginEmail.value.isBlank() || _loginPassword.value.isBlank()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.login(_loginEmail.value, _loginPassword.value)
            _isLoading.value = false
            result.onSuccess {
                _eventMessage.emit("Welcome back, ${it.name}!")
                onSuccess()
            }.onFailure {
                _errorMessage.value = it.message ?: "Authentication failed"
            }
        }
    }

    fun signUp(onSuccess: () -> Unit) {
        if (_signUpName.value.isBlank() || _signUpEmail.value.isBlank() || _signUpPassword.value.isBlank()) {
            _errorMessage.value = "All fields are required"
            return
        }
        if (_signUpPassword.value != _signUpConfirmPassword.value) {
            _errorMessage.value = "Passwords do not match"
            return
        }
        if (_signUpPassword.value.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.signUp(_signUpName.value, _signUpEmail.value, _signUpPassword.value)
            _isLoading.value = false
            result.onSuccess {
                _eventMessage.emit("Account created successfully!")
                onSuccess()
            }.onFailure {
                _errorMessage.value = it.message ?: "Registration failed"
            }
        }
    }

    fun sendPasswordReset(onSuccess: () -> Unit) {
        if (_forgotPasswordEmail.value.isBlank() || !_forgotPasswordEmail.value.contains("@")) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.sendPasswordReset(_forgotPasswordEmail.value)
            _isLoading.value = false
            result.onSuccess {
                _eventMessage.emit("Password reset link sent to your email")
                onSuccess()
            }.onFailure {
                _errorMessage.value = it.message ?: "Failed to send reset email"
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _eventMessage.emit("Logged out")
            onLoggedOut()
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            val result = authRepository.updateProfile(name, email)
            result.onSuccess {
                _eventMessage.emit("Profile updated successfully")
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }
}
