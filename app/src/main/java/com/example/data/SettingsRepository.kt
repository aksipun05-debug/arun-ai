package com.example.data

import com.example.model.AppLanguage
import com.example.model.AppSettings
import com.example.model.VoiceSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>
    fun setLanguage(language: AppLanguage)
    fun setVoice(voiceSettings: VoiceSettings)
    fun setMemoryEnabled(enabled: Boolean)
    fun setNotificationsEnabled(enabled: Boolean)
    fun setIncognitoMode(enabled: Boolean)
    fun setHapticFeedback(enabled: Boolean)
    fun clearMemory()
}

class DefaultSettingsRepository : SettingsRepository {
    private val _settings = MutableStateFlow(AppSettings())
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    override fun setLanguage(language: AppLanguage) {
        _settings.value = _settings.value.copy(language = language)
    }

    override fun setVoice(voiceSettings: VoiceSettings) {
        _settings.value = _settings.value.copy(voiceSettings = voiceSettings)
    }

    override fun setMemoryEnabled(enabled: Boolean) {
        _settings.value = _settings.value.copy(memoryEnabled = enabled)
    }

    override fun setNotificationsEnabled(enabled: Boolean) {
        _settings.value = _settings.value.copy(notificationsEnabled = enabled)
    }

    override fun setIncognitoMode(enabled: Boolean) {
        _settings.value = _settings.value.copy(incognitoMode = enabled)
    }

    override fun setHapticFeedback(enabled: Boolean) {
        _settings.value = _settings.value.copy(hapticFeedback = enabled)
    }

    override fun clearMemory() {
        // Clears memory state
    }
}
