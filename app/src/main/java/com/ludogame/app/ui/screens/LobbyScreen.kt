package com.ludogame.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LobbyScreen(
    roomId: String,
    onGameStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Lobby", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
        Text("Room Code: $roomId", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(48.dp))
        Text("Waiting for players…", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(48.dp))
        Button(onClick = onGameStart, modifier = Modifier.fillMaxWidth()) {
            Text("Start Game")
        }
    }
}
