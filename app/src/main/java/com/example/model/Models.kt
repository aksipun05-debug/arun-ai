package com.example.model

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी"),
    HINGLISH("hi-en", "Hinglish", "Hinglish"),
    ODIA("or", "Odia", "ଓଡ଼ିଆ")
}

enum class MessageSender {
    USER,
    ASSISTANT
}

enum class MessageStatus {
    SENDING,
    SENT,
    ERROR
}

enum class AttachmentType {
    IMAGE,
    DOCUMENT,
    CAMERA,
    AUDIO
}

data class AttachmentItem(
    val id: String,
    val name: String,
    val type: AttachmentType,
    val sizeText: String
)

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT,
    val attachments: List<AttachmentItem> = emptyList()
)

data class ChatSession(
    val id: String,
    val title: String,
    val lastMessageSnippet: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val messageCount: Int = 0,
    val language: AppLanguage = AppLanguage.ENGLISH
)

data class UserAccount(
    val id: String = "user_001",
    val name: String = "Arun Kumar",
    val email: String = "arun.ai@example.com",
    val tier: String = "Pro Assistant Plan",
    val chatsCount: Int = 24,
    val promptsCount: Int = 186
)

enum class QuickActionType {
    ASK_ANYTHING,
    VOICE_CHAT,
    IMAGE,
    PDF,
    CAMERA,
    WEB_SEARCH,
    REMINDER
}

data class QuickActionItem(
    val type: QuickActionType,
    val title: String,
    val subtitle: String,
    val promptSeed: String
)

data class VoiceSettings(
    val voiceName: String = "Arun Natural (Male)",
    val speedRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val autoPlayResponse: Boolean = false
)

data class AppSettings(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val voiceSettings: VoiceSettings = VoiceSettings(),
    val memoryEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val incognitoMode: Boolean = false,
    val hapticFeedback: Boolean = true
)
