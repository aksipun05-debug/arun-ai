package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.di.AppContainer
import com.example.ui.screens.ChatHistoryScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IndividualChatScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NewChatScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.SplashScreen

@Composable
fun ArunAppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val authViewModel = AppContainer.authViewModel
    val chatViewModel = AppContainer.chatViewModel
    val settingsViewModel = AppContainer.settingsViewModel

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH,
        modifier = modifier
    ) {
        // 1. SPLASH
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onTimeout = {
                    val target = if (isAuthenticated) NavRoutes.HOME else NavRoutes.LOGIN
                    navController.navigate(target) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // 2. LOGIN
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(NavRoutes.SIGN_UP)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(NavRoutes.FORGOT_PASSWORD)
                }
            )
        }

        // 3. SIGN UP
        composable(NavRoutes.SIGN_UP) {
            SignUpScreen(
                authViewModel = authViewModel,
                onSignUpSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.SIGN_UP) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 4. FORGOT PASSWORD
        composable(NavRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 5. HOME
        composable(NavRoutes.HOME) {
            HomeScreen(
                chatViewModel = chatViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToChat = { sessionId ->
                    navController.navigate(NavRoutes.chatRoute(sessionId))
                },
                onNavigateToNewChatWithPrompt = { prompt ->
                    val language = settingsViewModel.settings.value.language
                    chatViewModel.startNewChat(prompt.ifBlank { null }, language)
                    val id = chatViewModel.currentSessionId.value ?: "new"
                    navController.navigate(NavRoutes.chatRoute(id))
                },
                onNavigateToNewChatWithVoice = {
                    val language = settingsViewModel.settings.value.language
                    chatViewModel.startNewChat(null, language)
                    chatViewModel.toggleVoiceMode(true)
                    val id = chatViewModel.currentSessionId.value ?: "new"
                    navController.navigate(NavRoutes.chatRoute(id))
                },
                onNavigateToHistory = {
                    navController.navigate(NavRoutes.CHAT_HISTORY)
                },
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.PROFILE)
                },
                onNavigateToSettings = {
                    navController.navigate(NavRoutes.SETTINGS)
                }
            )
        }

        // 6. NEW CHAT
        composable(NavRoutes.NEW_CHAT) {
            NewChatScreen(
                chatViewModel = chatViewModel,
                settingsViewModel = settingsViewModel,
                onChatStarted = { sessionId ->
                    navController.navigate(NavRoutes.chatRoute(sessionId)) {
                        popUpTo(NavRoutes.NEW_CHAT) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME)
                },
                onNavigateToHistory = {
                    navController.navigate(NavRoutes.CHAT_HISTORY)
                },
                onNavigateToSettings = {
                    navController.navigate(NavRoutes.SETTINGS)
                },
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.PROFILE)
                }
            )
        }

        // 7. CHAT HISTORY
        composable(NavRoutes.CHAT_HISTORY) {
            ChatHistoryScreen(
                chatViewModel = chatViewModel,
                onOpenConversation = { sessionId ->
                    navController.navigate(NavRoutes.chatRoute(sessionId))
                },
                onNavigateToNewChat = {
                    navController.navigate(NavRoutes.NEW_CHAT)
                },
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME)
                },
                onNavigateToSettings = {
                    navController.navigate(NavRoutes.SETTINGS)
                },
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.PROFILE)
                }
            )
        }

        // 8. INDIVIDUAL CHAT
        composable(
            route = NavRoutes.CHAT_ROUTE,
            arguments = listOf(
                navArgument(NavRoutes.CHAT_PARAM) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString(NavRoutes.CHAT_PARAM) ?: ""
            IndividualChatScreen(
                sessionId = sessionId,
                chatViewModel = chatViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStartNewChat = {
                    val language = settingsViewModel.settings.value.language
                    chatViewModel.startNewChat(null, language)
                    val newId = chatViewModel.currentSessionId.value ?: sessionId
                    navController.navigate(NavRoutes.chatRoute(newId)) {
                        popUpTo(NavRoutes.chatRoute(sessionId)) { inclusive = true }
                    }
                }
            )
        }

        // 9. PROFILE
        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                authViewModel = authViewModel,
                chatViewModel = chatViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME)
                },
                onNavigateToNewChat = {
                    navController.navigate(NavRoutes.NEW_CHAT)
                },
                onNavigateToHistory = {
                    navController.navigate(NavRoutes.CHAT_HISTORY)
                },
                onNavigateToSettings = {
                    navController.navigate(NavRoutes.SETTINGS)
                },
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 10. SETTINGS
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                authViewModel = authViewModel,
                chatViewModel = chatViewModel,
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME)
                },
                onNavigateToNewChat = {
                    navController.navigate(NavRoutes.NEW_CHAT)
                },
                onNavigateToHistory = {
                    navController.navigate(NavRoutes.CHAT_HISTORY)
                },
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.PROFILE)
                },
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
