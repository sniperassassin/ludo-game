package com.ludogame.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ludogame.app.ui.board.DiceView
import com.ludogame.app.ui.board.LudoBoard
import com.ludogame.app.ui.theme.LudoBlue
import com.ludogame.app.ui.theme.LudoGreen
import com.ludogame.app.ui.theme.LudoRed
import com.ludogame.app.ui.theme.LudoYellow
import com.ludogame.app.ui.viewmodel.GameViewModel
import com.ludogame.core.model.GamePhase
import com.ludogame.core.model.PlayerColor

@Composable
fun GameScreen(
    roomId: String,
    viewModel: GameViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val movableTokenIds by viewModel.movableTokenIds.collectAsState()

    LaunchedEffect(roomId) { viewModel.startGame(roomId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        gameState?.let { state ->
            val currentPlayer = state.players.getOrNull(state.currentTurnIndex)
            val playerColor = when (currentPlayer?.color) {
                PlayerColor.RED    -> LudoRed
                PlayerColor.BLUE   -> LudoBlue
                PlayerColor.GREEN  -> LudoGreen
                PlayerColor.YELLOW -> LudoYellow
                else               -> Color.Gray
            }

            // Status bar — player name only (dice is now visible below)
            Surface(
                color = playerColor.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${currentPlayer?.name ?: "?"}'s turn",
                    style = MaterialTheme.typography.titleMedium,
                    color = playerColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(6.dp))

            // Board — weight(1f) lets it shrink so the dice always fits on screen
            LudoBoard(
                gameState = state,
                movableTokenIds = movableTokenIds,
                onTokenClick = { tokenId -> viewModel.moveToken(tokenId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(Modifier.height(12.dp))

            if (state.phase != GamePhase.FINISHED) {
                // 3-D dice — tappable only in ROLLING phase
                DiceView(
                    value = state.diceValue,
                    isRollEnabled = state.phase == GamePhase.ROLLING,
                    onRoll = { viewModel.rollDice() },
                    pipColor = playerColor,
                    modifier = Modifier.size(88.dp)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = when (state.phase) {
                        GamePhase.ROLLING -> "Tap the dice to roll"
                        GamePhase.MOVING  -> "Tap a highlighted token to move"
                        else              -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = playerColor,
                    textAlign = TextAlign.Center
                )
            } else {
                val winner = state.players.find { it.id == state.winnerId }
                Text(
                    text = "${winner?.name ?: "Someone"} wins!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = playerColor,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(8.dp))

        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
