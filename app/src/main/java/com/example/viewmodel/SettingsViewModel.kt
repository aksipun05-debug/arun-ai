package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SettingsRepository
import com.example.model.AppLanguage
import com.example.model.AppSettings
import com.example.model.VoiceSettings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings

    private val _eventMessage = MutableSharedFlow<String>()
    val eventMessage: SharedFlow<String> = _eventMessage.asSharedFlow()

    fun setLanguage(language: AppLanguage) {
        settingsRepository.setLanguage(language)
        viewModelScope.launch {
            _eventMessage.emit("Language changed to ${language.displayName}")
        }
    }

    fun setVoice(voiceSettings: VoiceSettings) {
        settingsRepository.setVoice(voiceSettings)
        viewModelScope.launch {
            _eventMessage.emit("Voice set to ${voiceSettings.voiceName}")
        }
    }

    fun toggleMemory(enabled: Boolean) {
        settingsRepository.setMemoryEnabled(enabled)
        viewModelScope.launch {
            _eventMessage.emit(if (enabled) "Memory personalization enabled" else "Memory disabled")
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        settingsRepository.setNotificationsEnabled(enabled)
    }

    fun toggleIncognito(enabled: Boolean) {
        settingsRepository.setIncognitoMode(enabled)
        viewModelScope.launch {
            _eventMessage.emit(if (enabled) "Incognito mode active (chats won't be saved)" else "Standard history restored")
        }
    }

    fun clearMemory() {
        settingsRepository.clearMemory()
        viewModelScope.launch {
            _eventMessage.emit("AI personal memory context cleared")
        }
    }
}
