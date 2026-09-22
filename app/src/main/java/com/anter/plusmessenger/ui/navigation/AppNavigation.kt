package com.anter.plusmessenger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anter.plusmessenger.ui.screens.chat.ChatScreen
import com.anter.plusmessenger.ui.screens.conversations.ConversationsScreen
import com.anter.plusmessenger.ui.screens.login.LoginScreen

object Routes {
    const val LOGIN = "login"
    const val CONVERSATIONS = "conversations"
    const val CHAT = "chat/{username}"
    fun chat(username: String) = "chat/$username"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authVm: AuthViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(onSuccess = {
                navController.navigate(Routes.CONVERSATIONS) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }

        composable(Routes.CONVERSATIONS) {
            ConversationsScreen(
                onLogout = {
                    authVm.logout {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.CONVERSATIONS) { inclusive = true }
                        }
                    }
                },
                onOpenChat = { username ->
                    navController.navigate(Routes.chat(username))
                }
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) {
            ChatScreen(onBack = { navController.popBackStack() })
        }
    }
}
