package com.anter.plusmessenger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anter.plusmessenger.ui.screens.conversations.ConversationsScreen
import com.anter.plusmessenger.ui.screens.login.LoginScreen

object Routes {
    const val LOGIN = "login"
    const val CONVERSATIONS = "conversations"
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
            ConversationsScreen(onLogout = {
                authVm.logout {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.CONVERSATIONS) { inclusive = true }
                    }
                }
            })
        }
    }
}
