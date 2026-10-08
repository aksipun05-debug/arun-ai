package com.example.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val FORGOT_PASSWORD = "forgot_password"
    const val HOME = "home"
    const val NEW_CHAT = "new_chat"
    const val CHAT_HISTORY = "chat_history"
    const val CHAT = "chat"
    const val CHAT_PARAM = "sessionId"
    const val CHAT_ROUTE = "$CHAT/{$CHAT_PARAM}"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    fun chatRoute(sessionId: String): String = "$CHAT/$sessionId"
}
