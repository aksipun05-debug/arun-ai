package com.example.di

import com.example.data.AuthRepository
import com.example.data.ChatRepository
import com.example.data.DefaultAuthRepository
import com.example.data.DefaultChatRepository
import com.example.data.DefaultSettingsRepository
import com.example.data.SettingsRepository
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.SettingsViewModel

object AppContainer {
    val authRepository: AuthRepository by lazy { DefaultAuthRepository() }
    val chatRepository: ChatRepository by lazy { DefaultChatRepository() }
    val settingsRepository: SettingsRepository by lazy { DefaultSettingsRepository() }

    val authViewModel: AuthViewModel by lazy { AuthViewModel(authRepository) }
    val chatViewModel: ChatViewModel by lazy { ChatViewModel(chatRepository) }
    val settingsViewModel: SettingsViewModel by lazy { SettingsViewModel(settingsRepository) }
}
