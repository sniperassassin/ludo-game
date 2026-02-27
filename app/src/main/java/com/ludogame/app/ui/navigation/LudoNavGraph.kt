package com.ludogame.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ludogame.app.ui.screens.GameScreen
import com.ludogame.app.ui.screens.HomeScreen
import com.ludogame.app.ui.screens.LobbyScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Lobby : Screen("lobby/{roomId}") {
        fun createRoute(roomId: String) = "lobby/$roomId"
    }
    object Game : Screen("game/{roomId}") {
        fun createRoute(roomId: String) = "game/$roomId"
    }
}

@Composable
fun LudoNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                onCreateRoom = { roomId -> navController.navigate(Screen.Lobby.createRoute(roomId)) },
                onJoinRoom   = { roomId -> navController.navigate(Screen.Lobby.createRoute(roomId)) }
            )
        }

        composable(Screen.Lobby.route) { back ->
            val roomId = back.arguments?.getString("roomId").orEmpty()
            LobbyScreen(
                roomId = roomId,
                onGameStart = { navController.navigate(Screen.Game.createRoute(roomId)) }
            )
        }

        composable(Screen.Game.route) { back ->
            val roomId = back.arguments?.getString("roomId").orEmpty()
            GameScreen(roomId = roomId)
        }
    }
}
