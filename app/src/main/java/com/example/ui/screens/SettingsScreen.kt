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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.navigation.NavRoutes
import com.example.ui.components.ArunBottomNav
import com.example.ui.components.ArunTopBar
import com.example.ui.components.LanguageSelectionDialog
import com.example.ui.components.VoiceSelectionDialog
import com.example.ui.theme.BorderLight
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OffWhiteBg
import com.example.ui.theme.SkyPrimary
import com.example.ui.theme.SkyPrimarySoft
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.White
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToNewChat: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by settingsViewModel.settings.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        settingsViewModel.eventMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            selectedLanguage = settings.language,
            onSelectLanguage = { settingsViewModel.setLanguage(it) },
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showVoiceDialog) {
        VoiceSelectionDialog(
            currentVoice = settings.voiceSettings,
            onSaveVoice = { settingsViewModel.setVoice(it) },
            onDismiss = { showVoiceDialog = false }
        )
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = {
                Text(text = "Clear All Conversations?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "This will erase all past chats and messages stored on this device. This cannot be undone.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        chatViewModel.clearAllHistory()
                        showClearHistoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = White
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SkyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "About Arun AI", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Arun AI Assistant v1.0.0",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "A modern, intelligent assistant designed for lightning-fast conversations across English, Hindi, Hinglish, and Odia.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Android-First Responsive Architecture\n• StateFlow & MVVM Data Pipeline\n• Voice & Attachment Ready",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyPrimary)
                ) {
                    Text("Done")
                }
            },
            containerColor = White
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = "Log Out of Arun AI?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Are you sure you want to log out?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout(onLogout)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    modifier = Modifier.testTag("confirm_settings_logout_button")
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = White
        )
    }

    Scaffold(
        modifier = modifier
            .testTag("settings_screen")
            .fillMaxSize(),
        containerColor = OffWhiteBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ArunTopBar(
                title = "Settings",
                subtitle = "Preferences & System",
                showBackButton = false,
                showBrandLogo = true
            )
        },
        bottomBar = {
            ArunBottomNav(
                currentRoute = NavRoutes.SETTINGS,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.HOME -> onNavigateToHome()
                        NavRoutes.NEW_CHAT -> onNavigateToNewChat()
                        NavRoutes.CHAT_HISTORY -> onNavigateToHistory()
                        NavRoutes.SETTINGS -> {}
                        NavRoutes.PROFILE -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. ACCOUNT
            SettingsGroupCard(title = "Account") {
                SettingsActionRow(
                    icon = Icons.Default.Person,
                    title = "Account Details",
                    subtitle = "${currentUser?.name ?: "User"} • ${currentUser?.email ?: ""}",
                    onClick = { onNavigateToProfile() },
                    testTag = "settings_account_row"
                )
            }

            // 2. LANGUAGE
            SettingsGroupCard(title = "Language") {
                SettingsActionRow(
                    icon = Icons.Default.Language,
                    title = "App & Assistant Language",
                    subtitle = "${settings.language.displayName} (${settings.language.nativeName})",
                    onClick = { showLanguageDialog = true },
                    testTag = "settings_language_row"
                )
            }

            // 3. VOICE
            SettingsGroupCard(title = "Voice") {
                SettingsActionRow(
                    icon = Icons.Default.RecordVoiceOver,
                    title = "Voice Persona",
                    subtitle = settings.voiceSettings.voiceName,
                    onClick = { showVoiceDialog = true },
                    testTag = "settings_voice_row"
                )
            }

            // 4. MEMORY
            SettingsGroupCard(title = "Memory & Context") {
                SettingsToggleRow(
                    icon = Icons.Default.Psychology,
                    title = "Personalized Memory",
                    subtitle = "Remember preferences across conversations",
                    checked = settings.memoryEnabled,
                    onCheckedChange = { settingsViewModel.toggleMemory(it) },
                    testTag = "settings_memory_toggle"
                )
                if (settings.memoryEnabled) {
                    SettingsActionRow(
                        icon = Icons.Default.DeleteSweep,
                        title = "Clear Saved Context",
                        subtitle = "Erase personalized facts learned by AI",
                        onClick = { settingsViewModel.clearMemory() },
                        testTag = "settings_clear_memory_row"
                    )
                }
            }

            // 5. NOTIFICATIONS
            SettingsGroupCard(title = "Notifications") {
                SettingsToggleRow(
                    icon = Icons.Default.Notifications,
                    title = "Daily Tips & Updates",
                    subtitle = "Receive periodic AI suggestions",
                    checked = settings.notificationsEnabled,
                    onCheckedChange = { settingsViewModel.toggleNotifications(it) },
                    testTag = "settings_notifications_toggle"
                )
            }

            // 6. PRIVACY
            SettingsGroupCard(title = "Privacy") {
                SettingsToggleRow(
                    icon = Icons.Default.Security,
                    title = "Incognito Mode",
                    subtitle = "Do not save new chats to history",
                    checked = settings.incognitoMode,
                    onCheckedChange = { settingsViewModel.toggleIncognito(it) },
                    testTag = "settings_incognito_toggle"
                )
            }

            // 7. CHAT HISTORY
            SettingsGroupCard(title = "Chat History") {
                SettingsActionRow(
                    icon = Icons.Default.DeleteSweep,
                    title = "Clear All Conversations",
                    subtitle = "Delete all stored chat threads",
                    onClick = { showClearHistoryDialog = true },
                    isDestructive = true,
                    testTag = "settings_clear_all_history_row"
                )
            }

            // 8. ABOUT ARUN AI
            SettingsGroupCard(title = "About Arun AI") {
                SettingsActionRow(
                    icon = Icons.Default.Info,
                    title = "About Arun AI",
                    subtitle = "Version 1.0.0 • Architecture & Credits",
                    onClick = { showAboutDialog = true },
                    testTag = "settings_about_row"
                )
            }

            // 9. LOGOUT
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_logout_card")
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { showLogoutDialog = true },
                color = White,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BorderLight),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ErrorRed.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = ErrorRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Log Out",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ErrorRed
                            )
                        )
                        Text(
                            text = "Sign out of your account on this device",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun SettingsGroupCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = SkyPrimary,
                fontSize = 13.sp
            ),
            modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = White,
            border = BorderStroke(1.dp, BorderLight),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
    testTag: String = ""
) {
    Row(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isDestructive) ErrorRed.copy(alpha = 0.1f) else SkyPrimarySoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDestructive) ErrorRed else SkyPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) ErrorRed else TextPrimary
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = BorderLight,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Row(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SkyPrimarySoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = SkyPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = SkyPrimary,
                uncheckedThumbColor = White,
                uncheckedTrackColor = BorderLight
            )
        )
    }
}
