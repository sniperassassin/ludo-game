package com.ludogame.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun HomeScreen(
    onCreateRoom: (String) -> Unit,
    onJoinRoom: (String) -> Unit
) {
    var joinCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ludo", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(48.dp))

        Button(
            onClick = { onCreateRoom(UUID.randomUUID().toString().take(6).uppercase()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Room")
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = joinCode,
            onValueChange = { joinCode = it.uppercase() },
            label = { Text("Room Code") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { if (joinCode.isNotBlank()) onJoinRoom(joinCode) },
            modifier = Modifier.fillMaxWidth(),
            enabled = joinCode.isNotBlank()
        ) {
            Text("Join Room")
        }
    }
}
