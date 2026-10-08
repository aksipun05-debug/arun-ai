package com.example.data

import com.example.model.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AuthRepository {
    val currentUser: StateFlow<UserAccount?>
    val isAuthenticated: StateFlow<Boolean>
    suspend fun login(email: String, password: String):Result<UserAccount>
    suspend fun signUp(name: String, email: String, password: String): Result<UserAccount>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun logout()
    suspend fun updateProfile(name: String, email: String): Result<UserAccount>
}

class DefaultAuthRepository : AuthRepository {
    private val _currentUser = MutableStateFlow<UserAccount?>(
        UserAccount(
            id = "user_arun_1",
            name = "Arun Kumar",
            email = "arun@example.com",
            tier = "Arun AI Pro",
            chatsCount = 18,
            promptsCount = 142
        )
    )
    override val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(true)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    override suspend fun login(email: String, password: String): Result<UserAccount> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password cannot be empty"))
        }
        val account = UserAccount(
            id = "user_${System.currentTimeMillis()}",
            name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
            email = email.trim()
        )
        _currentUser.value = account
        _isAuthenticated.value = true
        return Result.success(account)
    }

    override suspend fun signUp(name: String, email: String, password: String): Result<UserAccount> {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("All fields are required"))
        }
        val account = UserAccount(
            id = "user_${System.currentTimeMillis()}",
            name = name.trim(),
            email = email.trim()
        )
        _currentUser.value = account
        _isAuthenticated.value = true
        return Result.success(account)
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }
        return Result.success(Unit)
    }

    override suspend fun logout() {
        _currentUser.value = null
        _isAuthenticated.value = false
    }

    override suspend fun updateProfile(name: String, email: String): Result<UserAccount> {
        val current = _currentUser.value ?: return Result.failure(IllegalStateException("Not logged in"))
        val updated = current.copy(name = name.trim(), email = email.trim())
        _currentUser.value = updated
        return Result.success(updated)
    }
}
