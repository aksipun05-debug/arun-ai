package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.QuickActionType
import com.example.navigation.NavRoutes
import com.example.ui.components.ArunBottomNav
import com.example.ui.components.LanguageSelectionDialog
import com.example.ui.components.QuickActionCard
import com.example.ui.components.defaultQuickActions
import com.example.ui.theme.BorderLight
import com.example.ui.theme.OffWhiteBg
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimarySoft
import com.example.ui.theme.SkyPrimaryVariant
import com.example.ui.theme.SkySecondary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.White
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.SettingsViewModel

@Composable
fun HomeScreen(
    chatViewModel: ChatViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToChat: (sessionId: String) -> Unit,
    onNavigateToNewChatWithPrompt: (prompt: String) -> Unit,
    onNavigateToNewChatWithVoice: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sessions by chatViewModel.sessions.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            selectedLanguage = settings.language,
            onSelectLanguage = { settingsViewModel.setLanguage(it) },
            onDismiss = { showLanguageDialog = false }
        )
    }

    Scaffold(
        modifier = modifier
            .testTag("home_screen")
            .fillMaxSize(),
        containerColor = OffWhiteBg,
        bottomBar = {
            ArunBottomNav(
                currentRoute = NavRoutes.HOME,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.HOME -> {}
                        NavRoutes.NEW_CHAT -> onNavigateToNewChatWithPrompt("")
                        NavRoutes.CHAT_HISTORY -> onNavigateToHistory()
                        NavRoutes.SETTINGS -> onNavigateToSettings()
                        NavRoutes.PROFILE -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // TOP BAR SECTION
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SkyPrimarySoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Arun AI Logo",
                                tint = SkyPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Arun AI",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Language Pill Indicator
                        Surface(
                            modifier = Modifier
                                .testTag("home_language_pill")
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showLanguageDialog = true },
                            color = White,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, BorderLight),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = SkyPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = settings.language.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SkyPrimaryVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        // Profile Icon
                        Surface(
                            modifier = Modifier
                                .testTag("home_profile_button")
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { onNavigateToProfile() },
                            color = SkyPrimarySoft,
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = SkyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // GREETING BANNER
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = White,
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.dp, SkyPrimarySoft)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(White, SkyPrimarySoft.copy(alpha = 0.35f))
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Hi Arun AI 👋",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "How can I help you today?",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Start quick chat pill
                            Surface(
                                modifier = Modifier
                                    .testTag("home_quick_input_trigger")
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onNavigateToNewChatWithPrompt("") },
                                color = White,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, BorderLight),
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = TextTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Ask Arun AI anything...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextTertiary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // QUICK ACTIONS SECTION
            item {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )

                    // 2-row grid of quick actions: Ask Anything, Voice Chat, Image, PDF, Camera, Web Search, Reminder
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Row 1: Ask Anything & Voice Chat
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val a1 = defaultQuickActions[0] // Ask Anything
                            val a2 = defaultQuickActions[1] // Voice Chat
                            QuickActionCard(
                                action = a1,
                                onClick = { onNavigateToNewChatWithPrompt(a1.defaultPrompt) },
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionCard(
                                action = a2,
                                onClick = { onNavigateToNewChatWithVoice() },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Row 2: Image & PDF
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val a3 = defaultQuickActions[2] // Image
                            val a4 = defaultQuickActions[3] // PDF
                            QuickActionCard(
                                action = a3,
                                onClick = { onNavigateToNewChatWithPrompt(a3.defaultPrompt) },
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionCard(
                                action = a4,
                                onClick = { onNavigateToNewChatWithPrompt(a4.defaultPrompt) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Row 3: Camera & Web Search
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val a5 = defaultQuickActions[4] // Camera
                            val a6 = defaultQuickActions[5] // Web Search
                            QuickActionCard(
                                action = a5,
                                onClick = { onNavigateToNewChatWithPrompt(a5.defaultPrompt) },
                                modifier = Modifier.weight(1f)
                            )
                            QuickActionCard(
                                action = a6,
                                onClick = { onNavigateToNewChatWithPrompt(a6.defaultPrompt) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Row 4: Reminder (Full-width card)
                        val a7 = defaultQuickActions[6] // Reminder
                        QuickActionCard(
                            action = a7,
                            onClick = { onNavigateToNewChatWithPrompt(a7.defaultPrompt) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // SUGGESTED PROMPTS CHIPS
            item {
                Column(modifier = Modifier.padding(top = 22.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = SkyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Explore Ideas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    val suggestions = listOf(
                        "Explain quantum mechanics simply",
                        "Plan a weekend getaway in Odisha",
                        "Write a polite email asking for feedback",
                        "Create a 15-minute home workout routine",
                        "How do Kotlin Coroutines compare to Threads?"
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        items(suggestions) { prompt ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onNavigateToNewChatWithPrompt(prompt) },
                                color = White,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, BorderLight)
                            ) {
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // RECENT CONVERSATIONS PREVIEW
            item {
                Column(modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Chats",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        Text(
                            text = "See All",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SkyPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .testTag("home_see_all_history_button")
                                .clickable { onNavigateToHistory() }
                                .padding(4.dp)
                        )
                    }

                    if (sessions.isEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = White,
                            border = BorderStroke(1.dp, BorderLight)
                        ) {
                            Text(
                                text = "No recent chats yet. Start a new chat to begin!",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        sessions.take(3).forEach { session ->
                            Surface(
                                modifier = Modifier
                                    .testTag("recent_chat_item_${session.id}")
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onNavigateToChat(session.id) },
                                color = White,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, BorderLight)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(SkyPrimarySoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ChatBubbleOutline,
                                            contentDescription = null,
                                            tint = SkyPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = session.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextPrimary
                                            ),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = session.lastMessageSnippet,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontSize = 12.sp
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
