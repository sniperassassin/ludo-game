package com.ludogame.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ludogame.core.engine.BoardRules
import com.ludogame.core.engine.GameEngine
import com.ludogame.core.model.GamePhase
import com.ludogame.core.model.GameState
import com.ludogame.core.model.Player
import com.ludogame.core.model.PlayerColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val engine: GameEngine
) : ViewModel() {

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    private val _movableTokenIds = MutableStateFlow<List<Int>>(emptyList())
    val movableTokenIds: StateFlow<List<Int>> = _movableTokenIds.asStateFlow()

    fun startGame(roomId: String) {
        val players = listOf(
            Player("p1", "Red",    PlayerColor.RED),
            Player("p2", "Blue",   PlayerColor.BLUE),
            Player("p3", "Green",  PlayerColor.GREEN),
            Player("p4", "Yellow", PlayerColor.YELLOW),
        )
        _gameState.value = engine.startGame(players, roomId)
        refreshMovable()
    }

    fun rollDice() {
        val state = _gameState.value ?: return
        if (state.phase != GamePhase.ROLLING) return
        val rolled = engine.rollDice(state)
        _gameState.value = rolled
        // If no tokens can move after this roll, auto-skip to the next player
        val player = rolled.players[rolled.currentTurnIndex]
        val canMove = BoardRules.movableTokens(player.tokens, rolled.diceValue, player.color)
        if (canMove.isEmpty()) {
            val skipped = rolled.copy(
                currentTurnIndex = (rolled.currentTurnIndex + 1) % rolled.players.size,
                phase = GamePhase.ROLLING,
                diceValue = 0
            )
            _gameState.value = skipped
        }
        refreshMovable()
    }

    fun moveToken(tokenId: Int) {
        val state = _gameState.value ?: return
        if (state.phase != GamePhase.MOVING) return
        _gameState.value = engine.moveToken(state, tokenId)
        refreshMovable()
    }

    private fun refreshMovable() {
        val state = _gameState.value ?: return
        if (state.phase != GamePhase.MOVING) {
            _movableTokenIds.value = emptyList()
            return
        }
        val player = state.players[state.currentTurnIndex]
        _movableTokenIds.value = BoardRules
            .movableTokens(player.tokens, state.diceValue, player.color)
            .map { it.id }
    }
}
