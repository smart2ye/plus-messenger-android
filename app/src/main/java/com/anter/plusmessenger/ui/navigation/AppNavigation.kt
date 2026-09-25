package com.anter.plusmessenger.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anter.plusmessenger.ui.screens.chat.ChatScreen
import com.anter.plusmessenger.ui.screens.conversations.ConversationsScreen
import com.anter.plusmessenger.ui.screens.findfriends.FindFriendsScreen
import com.anter.plusmessenger.ui.screens.login.LoginScreen
import com.anter.plusmessenger.ui.screens.profile.UserProfileScreen
import com.anter.plusmessenger.ui.screens.settings.SettingsScreen

object Routes {
    const val LOGIN = "login"
    const val CONVERSATIONS = "conversations"
    const val CHAT = "chat/{username}"
    const val USER_PROFILE = "user/{username}"
    const val SETTINGS = "settings"
    const val FIND_FRIENDS = "find_friends"
    fun chat(username: String) = "chat/$username"
    fun userProfile(username: String) = "user/$username"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authVm: AuthViewModel = hiltViewModel()
    val authState by authVm.state.collectAsState()

    if (authState.checking) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) Routes.CONVERSATIONS else Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(onSuccess = {
                authVm.markLoggedIn()
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
                },
                onOpenProfile = { username ->
                    navController.navigate(Routes.userProfile(username))
                },
                onOpenSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onOpenFindFriends = {
                    navController.navigate(Routes.FIND_FRIENDS)
                }
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) {
            ChatScreen(
                onBack = { navController.popBackStack() },
                onOpenProfile = { username ->
                    navController.navigate(Routes.userProfile(username))
                }
            )
        }

        composable(
            route = Routes.USER_PROFILE,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) {
            UserProfileScreen(
                onBack = { navController.popBackStack() },
                onOpenChat = { username ->
                    navController.navigate(Routes.chat(username))
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.FIND_FRIENDS) {
            FindFriendsScreen(
                onBack = { navController.popBackStack() },
                onOpenProfile = { username ->
                    navController.navigate(Routes.userProfile(username))
                }
            )
        }
    }
}
