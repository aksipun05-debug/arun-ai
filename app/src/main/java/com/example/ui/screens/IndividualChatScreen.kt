package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttachmentType
import com.example.model.MessageSender
import com.example.ui.components.AttachmentBottomSheet
import com.example.ui.components.ChatBubble
import com.example.ui.components.RenameChatDialog
import com.example.ui.components.TypingIndicator
import com.example.ui.components.VoiceOverlay
import com.example.ui.theme.BorderLight
import com.example.ui.theme.OffWhiteBg
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimarySoft
import com.example.ui.theme.SkySecondary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.White
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualChatScreen(
    sessionId: String,
    chatViewModel: ChatViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onStartNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    val currentSessionId by chatViewModel.currentSessionId.collectAsState()
    val sessions by chatViewModel.sessions.collectAsState()
    val messages by chatViewModel.currentMessages.collectAsState()
    val inputText by chatViewModel.inputText.collectAsState()
    val isAITyping by chatViewModel.isAITyping.collectAsState()
    val isVoiceMode by chatViewModel.isVoiceMode.collectAsState()
    val isVoiceListening by chatViewModel.isVoiceListening.collectAsState()
    val audioAmplitudes by chatViewModel.audioAmplitudes.collectAsState()
    val attachments by chatViewModel.selectedAttachments.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()

    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    val currentSession = remember(sessions, currentSessionId) {
        sessions.find { it.id == currentSessionId }
    }

    // Back handler
    BackHandler {
        if (isVoiceMode) {
            chatViewModel.toggleVoiceMode(false)
        } else {
            onNavigateBack()
        }
    }

    LaunchedEffect(sessionId) {
        if (currentSessionId != sessionId) {
            chatViewModel.selectSession(sessionId)
        }
    }

    LaunchedEffect(messages.size, isAITyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(Unit) {
        chatViewModel.toastEvent.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (showAttachmentSheet) {
        AttachmentBottomSheet(
            onDismiss = { showAttachmentSheet = false },
            onSelectAttachment = { name, type, size ->
                chatViewModel.addAttachment(name, type, size)
            }
        )
    }

    if (showRenameDialog && currentSession != null) {
        RenameChatDialog(
            initialTitle = currentSession.title,
            onConfirm = { newTitle ->
                chatViewModel.renameSession(currentSession.id, newTitle)
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }

    Scaffold(
        modifier = modifier
            .testTag("individual_chat_screen")
            .fillMaxSize(),
        containerColor = OffWhiteBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentSession?.title ?: "Chat with Arun AI",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            ),
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(SkyPrimary)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Arun AI • ${settings.language.displayName}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("chat_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // New Chat option
                    IconButton(
                        onClick = onStartNewChat,
                        modifier = Modifier.testTag("chat_top_bar_new_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = "New Chat",
                            tint = SkyPrimary
                        )
                    }

                    // More Menu
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.testTag("chat_more_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = TextSecondary
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Rename Chat") },
                                onClick = {
                                    menuExpanded = false
                                    showRenameDialog = true
                                },
                                modifier = Modifier.testTag("chat_menu_rename")
                            )
                            DropdownMenuItem(
                                text = { Text("Share Conversation") },
                                onClick = {
                                    menuExpanded = false
                                    val summary = messages.joinToString("\n\n") {
                                        "${if (it.sender == MessageSender.USER) "User" else "Arun AI"}: ${it.content}"
                                    }
                                    shareText(context, summary)
                                },
                                modifier = Modifier.testTag("chat_menu_share")
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // MESSAGE LIST
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("chat_message_list"),
                    state = listState,
                    contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp)
                ) {
                    if (messages.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(SkyPrimarySoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = SkyPrimary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "How can Arun AI assist you?",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Type your prompt or tap the microphone to begin",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(messages, key = { it.id }) { message ->
                            ChatBubble(
                                message = message,
                                onCopy = { content ->
                                    copyToClipboard(context, content)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Copied response to clipboard")
                                    }
                                },
                                onRegenerate = { msgId ->
                                    chatViewModel.regenerateResponse(msgId, settings.language)
                                },
                                onShare = { content ->
                                    shareText(context, content)
                                }
                            )
                        }
                    }

                    // AI TYPING/LOADING INDICATOR
                    if (isAITyping) {
                        item {
                            TypingIndicator()
                        }
                    }
                }

                // ATTACHMENTS PREVIEW IN INPUT TRAY
                if (attachments.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(White)
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        attachments.forEach { att ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SkyPrimarySoft,
                                border = BorderStroke(1.dp, BorderLight)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📎 ${att.name}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SkyPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { chatViewModel.removeAttachment(att.id) },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove attachment",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // TEXT INPUT BAR (Mode: Typing)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    color = White,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Attachment Button (+)
                        IconButton(
                            onClick = { showAttachmentSheet = true },
                            modifier = Modifier
                                .testTag("chat_attachment_button")
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Attachment",
                                tint = SkyPrimary
                            )
                        }

                        // Text Field
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { chatViewModel.onInputTextChange(it) },
                            placeholder = {
                                Text(
                                    text = "Ask Arun AI...",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary)
                                )
                            },
                            singleLine = false,
                            maxLines = 4,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    chatViewModel.sendMessage(language = settings.language)
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_text_input")
                        )

                        // Mode Switcher: Mic Button (Voice mode)
                        IconButton(
                            onClick = { chatViewModel.toggleVoiceMode(true) },
                            modifier = Modifier
                                .testTag("chat_microphone_button")
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Switch to Voice Mode",
                                tint = SkyPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Send Button
                        val canSend = inputText.isNotBlank() || attachments.isNotEmpty()
                        IconButton(
                            onClick = {
                                if (canSend) {
                                    chatViewModel.sendMessage(language = settings.language)
                                }
                            },
                            enabled = canSend,
                            modifier = Modifier
                                .testTag("chat_send_button")
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (canSend) SkyPrimary else SkyPrimarySoft)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Message",
                                tint = if (canSend) White else TextTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // VOICE MODE OVERLAY (Mode: Voice)
            AnimatedVisibility(
                visible = isVoiceMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    VoiceOverlay(
                        isListening = isVoiceListening,
                        amplitudes = audioAmplitudes,
                        onFinishVoice = { spokenText ->
                            chatViewModel.sendVoicePrompt(spokenText, settings.language)
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

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Arun AI Response", text)
    clipboard?.setPrimaryClip(clip)
}

private fun shareText(context: Context, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share with")
    context.startActivity(shareIntent)
}
