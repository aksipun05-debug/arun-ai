package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttachmentType
import com.example.navigation.NavRoutes
import com.example.ui.components.ArunBottomNav
import com.example.ui.components.ArunTopBar
import com.example.ui.components.AttachmentBottomSheet
import com.example.ui.components.VoiceOverlay
import com.example.ui.theme.BorderLight
import com.example.ui.theme.OffWhiteBg
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimarySoft
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.White
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.SettingsViewModel

@Composable
fun NewChatScreen(
    chatViewModel: ChatViewModel,
    settingsViewModel: SettingsViewModel,
    onChatStarted: (sessionId: String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    var promptInput by remember { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    val isVoiceMode by chatViewModel.isVoiceMode.collectAsState()
    val isVoiceListening by chatViewModel.isVoiceListening.collectAsState()
    val amplitudes by chatViewModel.audioAmplitudes.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()

    val starterPrompts = listOf(
        "💡 Explain quantum computing in 3 simple bullet points",
        "✍️ Help me write a professional thank you email",
        "🌍 What are the top places to visit in Odisha?",
        "⚡ Compare Kotlin Coroutines with Java Threads"
    )

    if (showAttachmentSheet) {
        AttachmentBottomSheet(
            onDismiss = { showAttachmentSheet = false },
            onSelectAttachment = { name, type, size ->
                chatViewModel.addAttachment(name, type, size)
                chatViewModel.startNewChat("Attached $name: Please review and summarize", settings.language)
                chatViewModel.currentSessionId.value?.let { onChatStarted(it) }
            }
        )
    }

    Scaffold(
        modifier = modifier
            .testTag("new_chat_screen")
            .fillMaxSize(),
        containerColor = OffWhiteBg,
        topBar = {
            ArunTopBar(
                title = "New Conversation",
                subtitle = "Arun AI Assistant",
                showBackButton = true,
                onBackClick = onNavigateBack,
                showBrandLogo = false
            )
        },
        bottomBar = {
            ArunBottomNav(
                currentRoute = NavRoutes.NEW_CHAT,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.HOME -> onNavigateToHome()
                        NavRoutes.NEW_CHAT -> {}
                        NavRoutes.CHAT_HISTORY -> onNavigateToHistory()
                        NavRoutes.SETTINGS -> onNavigateToSettings()
                        NavRoutes.PROFILE -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(SkyPrimarySoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "New Chat",
                        tint = SkyPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "How can Arun AI assist you?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                Text(
                    text = "Ready in ${settings.language.displayName} • Typing & Voice enabled",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // Prompt Starters
                Text(
                    text = "Popular Starters",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    starterPrompts.forEach { prompt ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    chatViewModel.startNewChat(prompt, settings.language)
                                    val id = chatViewModel.currentSessionId.value
                                    if (id != null) onChatStarted(id)
                                },
                            color = White,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, BorderLight),
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Input Area on New Chat Screen
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = White,
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = { Text("Ask anything to start chat...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_chat_text_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Attachment (+)
                                IconButton(
                                    onClick = { showAttachmentSheet = true },
                                    modifier = Modifier.testTag("new_chat_attachment_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Attachment",
                                        tint = SkyPrimary
                                    )
                                }

                                // Mic Button (Voice mode)
                                IconButton(
                                    onClick = { chatViewModel.toggleVoiceMode(true) },
                                    modifier = Modifier.testTag("new_chat_mic_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Mode",
                                        tint = SkyPrimary
                                    )
                                }
                            }

                            // Send Button
                            IconButton(
                                onClick = {
                                    if (promptInput.isNotBlank()) {
                                        val text = promptInput
                                        promptInput = ""
                                        chatViewModel.startNewChat(text, settings.language)
                                        val id = chatViewModel.currentSessionId.value
                                        if (id != null) onChatStarted(id)
                                    }
                                },
                                enabled = promptInput.isNotBlank(),
                                modifier = Modifier
                                    .testTag("new_chat_send_button")
                                    .clip(CircleShape)
                                    .background(if (promptInput.isNotBlank()) SkyPrimary else SkyPrimarySoft)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = if (promptInput.isNotBlank()) White else TextTertiary
                                )
                            }
                        }
                    }
                }
            }

            // Voice Mode Overlay if active
            if (isVoiceMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    VoiceOverlay(
                        isListening = isVoiceListening,
                        amplitudes = amplitudes,
                        onFinishVoice = { spokenText ->
                            chatViewModel.startNewChat(spokenText, settings.language)
                            chatViewModel.toggleVoiceMode(false)
                            val id = chatViewModel.currentSessionId.value
                            if (id != null) onChatStarted(id)
                        },
                        onCancelVoice = {
                            chatViewModel.toggleVoiceMode(false)
                        }
                    )
                }
            }
        }
    }
}
