package com.example.data

import com.example.model.AppLanguage
import com.example.model.AttachmentItem
import com.example.model.ChatMessage
import com.example.model.ChatSession
import com.example.model.MessageSender
import com.example.model.MessageStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

interface ChatRepository {
    val sessions: StateFlow<List<ChatSession>>
    fun getMessages(sessionId: String): StateFlow<List<ChatMessage>>
    suspend fun createNewSession(initialTitle: String = "New Conversation", language: AppLanguage = AppLanguage.ENGLISH): String
    suspend fun sendMessage(
        sessionId: String,
        userText: String,
        attachments: List<AttachmentItem> = emptyList(),
        language: AppLanguage = AppLanguage.ENGLISH
    ): Result<ChatMessage>
    suspend fun regenerateResponse(
        sessionId: String,
        messageId: String,
        language: AppLanguage = AppLanguage.ENGLISH
    ): Result<ChatMessage>
    suspend fun renameSession(sessionId: String, newTitle: String)
    suspend fun deleteSession(sessionId: String)
    suspend fun clearAllSessions()
}

class DefaultChatRepository : ChatRepository {

    private val _sessions = MutableStateFlow<List<ChatSession>>(emptyList())
    override val sessions: StateFlow<List<ChatSession>> = _sessions.asStateFlow()

    private val messagesStore = mutableMapOf<String, MutableStateFlow<List<ChatMessage>>>()

    init {
        bootstrapInitialData()
    }

    private fun bootstrapInitialData() {
        val now = System.currentTimeMillis()
        val s1Id = "session_welcome"
        val s2Id = "session_code"
        val s3Id = "session_travel"

        val initialSessions = listOf(
            ChatSession(
                id = s1Id,
                title = "Exploring Arun AI features",
                lastMessageSnippet = "Here are three ways I can assist your daily productivity...",
                updatedAt = now - 1000 * 60 * 15, // 15 mins ago
                messageCount = 2,
                language = AppLanguage.ENGLISH
            ),
            ChatSession(
                id = s2Id,
                title = "Kotlin Coroutines Architecture",
                lastMessageSnippet = "StateFlow ensures predictable UI updates with Jetpack Compose.",
                updatedAt = now - 1000 * 60 * 60 * 4, // 4 hours ago
                messageCount = 4,
                language = AppLanguage.ENGLISH
            ),
            ChatSession(
                id = s3Id,
                title = "Trip plan to Puri & Konark",
                lastMessageSnippet = "Here is a 3-day itinerary covering the Golden Triangle of Odisha...",
                updatedAt = now - 1000 * 60 * 60 * 28, // yesterday
                messageCount = 3,
                language = AppLanguage.ODIA
            )
        )

        _sessions.value = initialSessions

        messagesStore[s1Id] = MutableStateFlow(
            listOf(
                ChatMessage(
                    id = "msg_s1_1",
                    sender = MessageSender.USER,
                    content = "What can you do for me as Arun AI?",
                    timestamp = now - 1000 * 60 * 16,
                    status = MessageStatus.SENT
                ),
                ChatMessage(
                    id = "msg_s1_2",
                    sender = MessageSender.ASSISTANT,
                    content = "Hello! I am Arun AI, your modern personal AI companion.\n\n" +
                            "I can help you with:\n" +
                            "• Brainstorming and drafting ideas\n" +
                            "• Multi-language assistance (English, Hindi, Hinglish, Odia)\n" +
                            "• Voice and text interactions\n" +
                            "• Structuring daily tasks, notes, and questions\n\n" +
                            "How would you like to get started today?",
                    timestamp = now - 1000 * 60 * 15,
                    status = MessageStatus.SENT
                )
            )
        )

        messagesStore[s2Id] = MutableStateFlow(
            listOf(
                ChatMessage(
                    id = "msg_s2_1",
                    sender = MessageSender.USER,
                    content = "Explain how StateFlow differs from LiveData in modern Compose.",
                    timestamp = now - 1000 * 60 * 60 * 5,
                    status = MessageStatus.SENT
                ),
                ChatMessage(
                    id = "msg_s2_2",
                    sender = MessageSender.ASSISTANT,
                    content = "In modern Android development with Jetpack Compose:\n\n" +
                            "1. **Pure Kotlin**: StateFlow is part of Kotlin Coroutines and does not require Android framework classes.\n" +
                            "2. **Compose Friendly**: Works seamlessly with `collectAsStateWithLifecycle()`.\n" +
                            "3. **Predictable Initial State**: StateFlow always holds an explicit non-null initial state value.",
                    timestamp = now - 1000 * 60 * 60 * 4,
                    status = MessageStatus.SENT
                )
            )
        )

        messagesStore[s3Id] = MutableStateFlow(
            listOf(
                ChatMessage(
                    id = "msg_s3_1",
                    sender = MessageSender.USER,
                    content = "ପୁରୀ ଏବଂ କୋଣାର୍କ ବୁଲିବା ପାଇଁ ଏକ ସୁନ୍ଦର ଯୋଜନା କୁହନ୍ତୁ।",
                    timestamp = now - 1000 * 60 * 60 * 29,
                    status = MessageStatus.SENT
                ),
                ChatMessage(
                    id = "msg_s3_2",
                    sender = MessageSender.ASSISTANT,
                    content = "ନମସ୍କାର! ପୁରୀ ଏବଂ କୋଣାର୍କ ଭ୍ରମଣ ପାଇଁ ୩ ଦିନିଆ ସୁନ୍ଦର ଯୋଜନା:\n\n" +
                            "• ଦିନ ୧: ଶ୍ରୀ ଜଗନ୍ନାଥ ମନ୍ଦିର ଦର୍ଶନ ଏବଂ ସୁନାର ଗୌଡ଼ିଆ ସମୁଦ୍ରକୂଳ (Golden Beach)\n" +
                            "• ଦିନ ୨: କୋଣାର୍କ ସୂର୍ଯ୍ୟ ମନ୍ଦିର ଏବଂ ଚନ୍ଦ୍ରଭାଗା ବିଚ୍ ର ମନୋରମ ସୂର୍ଯ୍ୟୋଦୟ\n" +
                            "• ଦିନ ୩: ଚିଲିକା ହ୍ରଦ (ସାତପଡ଼ା) ଡଲଫିନ ଦର୍ଶନ ଏବଂ ସ୍ଥାନୀୟ ଓଡ଼ିଆ ଖାଦ୍ୟ।",
                    timestamp = now - 1000 * 60 * 60 * 28,
                    status = MessageStatus.SENT
                )
            )
        )
    }

    override fun getMessages(sessionId: String): StateFlow<List<ChatMessage>> {
        return messagesStore.getOrPut(sessionId) {
            MutableStateFlow(emptyList())
        }.asStateFlow()
    }

    override suspend fun createNewSession(initialTitle: String, language: AppLanguage): String {
        val newId = "session_${UUID.randomUUID()}"
        val newSession = ChatSession(
            id = newId,
            title = initialTitle,
            lastMessageSnippet = "Conversation started",
            updatedAt = System.currentTimeMillis(),
            messageCount = 0,
            language = language
        )
        _sessions.value = listOf(newSession) + _sessions.value
        messagesStore[newId] = MutableStateFlow(emptyList())
        return newId
    }

    override suspend fun sendMessage(
        sessionId: String,
        userText: String,
        attachments: List<AttachmentItem>,
        language: AppLanguage
    ): Result<ChatMessage> {
        val now = System.currentTimeMillis()
        val userMsg = ChatMessage(
            id = "msg_user_${UUID.randomUUID()}",
            sender = MessageSender.USER,
            content = userText,
            timestamp = now,
            status = MessageStatus.SENT,
            attachments = attachments
        )

        val flow = messagesStore.getOrPut(sessionId) { MutableStateFlow(emptyList()) }
        flow.value = flow.value + userMsg

        updateSessionSnippet(sessionId, userText, flow.value.size)

        // Realistic assistant thinking duration for UI typing indicator
        delay(800)

        val responseContent = generateResponseForInput(userText, language, attachments)
        val assistantMsg = ChatMessage(
            id = "msg_ai_${UUID.randomUUID()}",
            sender = MessageSender.ASSISTANT,
            content = responseContent,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENT
        )

        flow.value = flow.value + assistantMsg
        updateSessionSnippet(sessionId, responseContent.take(60) + "...", flow.value.size)

        return Result.success(assistantMsg)
    }

    override suspend fun regenerateResponse(
        sessionId: String,
        messageId: String,
        language: AppLanguage
    ): Result<ChatMessage> {
        val flow = messagesStore[sessionId] ?: return Result.failure(IllegalStateException("Session not found"))
        val currentList = flow.value

        // Find last user message before this assistant message or matching
        val targetIndex = currentList.indexOfFirst { it.id == messageId }
        val userPrompt = if (targetIndex > 0 && currentList[targetIndex - 1].sender == MessageSender.USER) {
            currentList[targetIndex - 1].content
        } else {
            "Please refine your previous answer."
        }

        delay(900)

        val refreshedContent = generateResponseForInput(userPrompt, language, emptyList(), isRegenerate = true)
        val newAssistantMsg = ChatMessage(
            id = "msg_ai_${UUID.randomUUID()}",
            sender = MessageSender.ASSISTANT,
            content = refreshedContent,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENT
        )

        if (targetIndex >= 0) {
            val updated = currentList.toMutableList()
            updated[targetIndex] = newAssistantMsg
            flow.value = updated
        } else {
            flow.value = currentList + newAssistantMsg
        }

        return Result.success(newAssistantMsg)
    }

    override suspend fun renameSession(sessionId: String, newTitle: String) {
        _sessions.value = _sessions.value.map { session ->
            if (session.id == sessionId) session.copy(title = newTitle.trim()) else session
        }
    }

    override suspend fun deleteSession(sessionId: String) {
        _sessions.value = _sessions.value.filterNot { it.id == sessionId }
        messagesStore.remove(sessionId)
    }

    override suspend fun clearAllSessions() {
        _sessions.value = emptyList()
        messagesStore.clear()
    }

    private fun updateSessionSnippet(sessionId: String, snippet: String, count: Int) {
        _sessions.value = _sessions.value.map { session ->
            if (session.id == sessionId) {
                session.copy(
                    lastMessageSnippet = snippet,
                    updatedAt = System.currentTimeMillis(),
                    messageCount = count
                )
            } else session
        }.sortedByDescending { it.updatedAt }
    }

    private fun generateResponseForInput(
        input: String,
        language: AppLanguage,
        attachments: List<AttachmentItem>,
        isRegenerate: Boolean = false
    ): String {
        val prefix = if (isRegenerate) "Here is an alternative perspective:\n\n" else ""
        val attachmentNotice = if (attachments.isNotEmpty()) {
            "📎 Attached: ${attachments.joinToString { it.name }}\n\n"
        } else ""

        return when (language) {
            AppLanguage.HINDI -> {
                "${attachmentNotice}${prefix}नमस्ते! मैं अरुण एआई (Arun AI) हूँ। आपके प्रश्न \"$input\" के संदर्भ में:\n\n" +
                        "• मुख्य बिंदु: आपकी आवश्यकता को समझ लिया गया है।\n" +
                        "• सुझाव: आप इस विषय पर अधिक विवरण या अगला चरण पूछ सकते हैं।"
            }
            AppLanguage.HINGLISH -> {
                "${attachmentNotice}${prefix}Hey there! Arun AI here. Regarding \"$input\":\n\n" +
                        "• Summary: Maine aapka prompt process kar liya hai.\n" +
                        "• Next Steps: Bataiye aur kya detail me explore karna chahte hain?"
            }
            AppLanguage.ODIA -> {
                "${attachmentNotice}${prefix}ନମସ୍କାର! ମୁଁ ଅରୁଣ ଏଆଇ (Arun AI)। ଆପଣଙ୍କ ପ୍ରଶ୍ନ \"$input\" ପାଇଁ:\n\n" +
                        "• ମୁଖ୍ୟ ତଥ୍ୟ: ଆପଣଙ୍କ ଅନୁରୋଧ ସଠିକ୍ ଭାବେ ଗ୍ରହଣ କରାଯାଇଛି।\n" +
                        "• ପରବର୍ତ୍ତୀ ପଦକ୍ଷେପ: ଆପଣ ଅଧିକ ବିବରଣୀ ଜାଣିବାକୁ ଚାହିଁଲେ ପଚାରିପାରିବେ।"
            }
            AppLanguage.ENGLISH -> {
                "${attachmentNotice}${prefix}$input\n\n" +
                        "Thank you for sharing your prompt with Arun AI. Here are the core insights:\n\n" +
                        "1. **Analysis**: Your inquiry is structured and ready for processing.\n" +
                        "2. **Recommendation**: You can easily copy, share, or continue refining this discussion.\n" +
                        "3. **Integration Ready**: This clean architecture is prepared for live AI models to stream responses."
            }
        }
    }
}
