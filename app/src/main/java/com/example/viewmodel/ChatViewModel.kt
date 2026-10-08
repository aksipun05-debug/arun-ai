package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatRepository
import com.example.model.AppLanguage
import com.example.model.AttachmentItem
import com.example.model.AttachmentType
import com.example.model.ChatMessage
import com.example.model.ChatSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val sessions: StateFlow<List<ChatSession>> = chatRepository.sessions

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val currentMessages: StateFlow<List<ChatMessage>> = _currentMessages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isAITyping = MutableStateFlow(false)
    val isAITyping: StateFlow<Boolean> = _isAITyping.asStateFlow()

    // Mode switch: typing vs voice
    private val _isVoiceMode = MutableStateFlow(false)
    val isVoiceMode: StateFlow<Boolean> = _isVoiceMode.asStateFlow()

    private val _isVoiceListening = MutableStateFlow(false)
    val isVoiceListening: StateFlow<Boolean> = _isVoiceListening.asStateFlow()

    private val _audioAmplitudes = MutableStateFlow<List<Float>>(listOf(0.2f, 0.4f, 0.7f, 0.5f, 0.8f, 0.3f, 0.6f))
    val audioAmplitudes: StateFlow<List<Float>> = _audioAmplitudes.asStateFlow()

    private val _selectedAttachments = MutableStateFlow<List<AttachmentItem>>(emptyList())
    val selectedAttachments: StateFlow<List<AttachmentItem>> = _selectedAttachments.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private var voiceAnimationJob: Job? = null
    private var observeMessagesJob: Job? = null

    init {
        // Default to first session if available
        if (sessions.value.isNotEmpty()) {
            selectSession(sessions.value.first().id)
        }
    }

    fun selectSession(sessionId: String) {
        _currentSessionId.value = sessionId
        observeMessagesJob?.cancel()
        observeMessagesJob = viewModelScope.launch {
            chatRepository.getMessages(sessionId).collect { list ->
                _currentMessages.value = list
            }
        }
    }

    fun startNewChat(initialPrompt: String? = null, language: AppLanguage = AppLanguage.ENGLISH) {
        viewModelScope.launch {
            val title = if (!initialPrompt.isNullOrBlank()) {
                initialPrompt.take(28) + if (initialPrompt.length > 28) "..." else ""
            } else {
                "New Chat ${sessions.value.size + 1}"
            }
            val newId = chatRepository.createNewSession(title, language)
            selectSession(newId)
            if (!initialPrompt.isNullOrBlank()) {
                sendMessage(promptOverride = initialPrompt, language = language)
            }
        }
    }

    fun onInputTextChange(text: String) {
        _inputText.value = text
    }

    fun toggleVoiceMode(enabled: Boolean? = null) {
        val next = enabled ?: !_isVoiceMode.value
        _isVoiceMode.value = next
        if (next) {
            startVoiceListening()
        } else {
            stopVoiceListening()
        }
    }

    fun startVoiceListening() {
        _isVoiceListening.value = true
        voiceAnimationJob?.cancel()
        voiceAnimationJob = viewModelScope.launch {
            val random = java.util.Random()
            while (isActive && _isVoiceListening.value) {
                val newAmps = List(8) { 0.15f + random.nextFloat() * 0.85f }
                _audioAmplitudes.value = newAmps
                delay(120)
            }
        }
    }

    fun stopVoiceListening() {
        _isVoiceListening.value = false
        voiceAnimationJob?.cancel()
        _audioAmplitudes.value = listOf(0.2f, 0.3f, 0.4f, 0.3f, 0.2f)
    }

    fun sendVoicePrompt(spokenText: String, language: AppLanguage = AppLanguage.ENGLISH) {
        stopVoiceListening()
        _isVoiceMode.value = false
        _inputText.value = spokenText
        sendMessage(language = language)
    }

    fun addAttachment(name: String, type: AttachmentType, sizeText: String) {
        val item = AttachmentItem(
            id = "att_${UUID.randomUUID()}",
            name = name,
            type = type,
            sizeText = sizeText
        )
        _selectedAttachments.value = _selectedAttachments.value + item
    }

    fun removeAttachment(id: String) {
        _selectedAttachments.value = _selectedAttachments.value.filterNot { it.id == id }
    }

    fun clearAttachments() {
        _selectedAttachments.value = emptyList()
    }

    fun sendMessage(
        promptOverride: String? = null,
        language: AppLanguage = AppLanguage.ENGLISH
    ) {
        val textToSend = (promptOverride ?: _inputText.value).trim()
        val attachmentsToSend = _selectedAttachments.value
        if (textToSend.isBlank() && attachmentsToSend.isEmpty()) return

        var activeId = _currentSessionId.value
        viewModelScope.launch {
            if (activeId == null) {
                val title = textToSend.take(28).ifBlank { "New Chat" }
                activeId = chatRepository.createNewSession(title, language)
                selectSession(activeId!!)
            }

            _inputText.value = ""
            _selectedAttachments.value = emptyList()
            _isAITyping.value = true

            try {
                chatRepository.sendMessage(
                    sessionId = activeId!!,
                    userText = textToSend,
                    attachments = attachmentsToSend,
                    language = language
                )
            } catch (e: Exception) {
                _toastEvent.emit("Failed to send message: ${e.message}")
            } finally {
                _isAITyping.value = false
            }
        }
    }

    fun regenerateResponse(messageId: String, language: AppLanguage = AppLanguage.ENGLISH) {
        val activeId = _currentSessionId.value ?: return
        viewModelScope.launch {
            _isAITyping.value = true
            try {
                chatRepository.regenerateResponse(activeId, messageId, language)
                _toastEvent.emit("Response regenerated")
            } catch (e: Exception) {
                _toastEvent.emit("Failed to regenerate: ${e.message}")
            } finally {
                _isAITyping.value = false
            }
        }
    }

    fun renameSession(sessionId: String, newTitle: String) {
        viewModelScope.launch {
            chatRepository.renameSession(sessionId, newTitle)
            _toastEvent.emit("Conversation renamed")
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            chatRepository.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                val remaining = sessions.value.filterNot { it.id == sessionId }
                if (remaining.isNotEmpty()) {
                    selectSession(remaining.first().id)
                } else {
                    _currentSessionId.value = null
                    _currentMessages.value = emptyList()
                }
            }
            _toastEvent.emit("Conversation deleted")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            chatRepository.clearAllSessions()
            _currentSessionId.value = null
            _currentMessages.value = emptyList()
            _toastEvent.emit("Chat history cleared")
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }
}
