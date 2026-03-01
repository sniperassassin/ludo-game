package com.ludogame.core.engine

import com.ludogame.core.model.GamePhase
import com.ludogame.core.model.GameState
import com.ludogame.core.model.Player
import com.ludogame.core.model.Token
import com.ludogame.core.model.TokenStatus

class GameEngine(private val diceRoller: DiceRoller = DiceRoller()) {

    fun startGame(players: List<Player>, roomId: String = ""): GameState {
        require(players.size in 2..4) { "Ludo requires 2–4 players" }
        return GameState(
            roomId = roomId,
            players = players,
            phase = GamePhase.ROLLING,
            currentTurnIndex = 0
        )
    }

    fun rollDice(state: GameState): GameState {
        require(state.phase == GamePhase.ROLLING) { "Not in rolling phase" }
        val value = diceRoller.roll()
        
        val newConsecutiveSixes = if (value == 6) state.consecutiveSixes + 1 else 0
        
        return if (newConsecutiveSixes == 3) {
            // Three sixes in a row: turn ends immediately, no move allowed
            state.copy(
                diceValue = value,
                consecutiveSixes = 0,
                currentTurnIndex = (state.currentTurnIndex + 1) % state.players.size,
                phase = GamePhase.ROLLING
            )
        } else {
            state.copy(
                diceValue = value,
                consecutiveSixes = newConsecutiveSixes,
                phase = GamePhase.MOVING
            )
        }
    }

    fun moveToken(state: GameState, tokenId: Int): GameState {
        require(state.phase == GamePhase.MOVING) { "Not in moving phase" }
        val currentPlayer = state.players[state.currentTurnIndex]
        
        // CRITICAL FIX: Only allow moving tokens that belong to the current player
        val token = currentPlayer.tokens.find { it.id == tokenId } 
            ?: return state.copy(phase = GamePhase.MOVING) // Ignore if not current player's token

        val updatedToken = applyMove(token, state.diceValue, currentPlayer)
        
        // If move was invalid (e.g. overshoot), don't update state
        if (updatedToken == token && token.status != TokenStatus.HOME) {
             return state
        }

        val updatedPlayer = currentPlayer.copy(
            tokens = currentPlayer.tokens.map { if (it.id == tokenId) updatedToken else it }
        )

        var captureOccurred = false
        var updatedPlayers = state.players.map {
            if (it.id == currentPlayer.id) updatedPlayer else it
        }

        // Capture: if the moved token landed on a non-safe main-track square,
        // send any opponent token on that square back to their home yard.
        if (updatedToken.status == TokenStatus.ACTIVE &&
            updatedToken.position in 0 until BoardRules.TRACK_SIZE &&
            !BoardRules.isSafe(updatedToken.position)
        ) {
            updatedPlayers = updatedPlayers.map { p ->
                if (p.id == currentPlayer.id) p
                else {
                    var capturedInThisPlayer = false
                    val newTokens = p.tokens.map { t ->
                        if (t.status == TokenStatus.ACTIVE && t.position == updatedToken.position) {
                            capturedInThisPlayer = true
                            t.copy(position = -1, status = TokenStatus.HOME)
                        } else t
                    }
                    if (capturedInThisPlayer) captureOccurred = true
                    p.copy(tokens = newTokens)
                }
            }
        }

        val tokenFinished = token.status != TokenStatus.FINISHED && updatedToken.status == TokenStatus.FINISHED
        val winner = updatedPlayers.find { p -> p.tokens.all { it.status == TokenStatus.FINISHED } }

        // Extra turn rules: rolling a 6, capturing an opponent, or finishing a token
        val grantExtraTurn = state.diceValue == 6 || captureOccurred || tokenFinished
        
        val nextTurnIndex = if (grantExtraTurn && winner == null) {
            state.currentTurnIndex
        } else {
            (state.currentTurnIndex + 1) % state.players.size
        }

        // Reset consecutive sixes if turn changes
        val finalConsecutiveSixes = if (nextTurnIndex == state.currentTurnIndex) state.consecutiveSixes else 0

        return state.copy(
            players = updatedPlayers,
            currentTurnIndex = nextTurnIndex,
            consecutiveSixes = finalConsecutiveSixes,
            phase = if (winner != null) GamePhase.FINISHED else GamePhase.ROLLING,
            winnerId = winner?.id,
            diceValue = if (nextTurnIndex != state.currentTurnIndex) 0 else state.diceValue
        )
    }

    private fun applyMove(token: Token, diceValue: Int, player: Player): Token {
        if (token.status == TokenStatus.FINISHED) return token

        val startPos = BoardRules.startPositions[player.color] ?: 0

        if (token.status == TokenStatus.HOME) {
            return if (BoardRules.canExitHome(diceValue)) {
                token.copy(position = startPos, status = TokenStatus.ACTIVE)
            } else token
        }

        // TokenStatus.ACTIVE
        val relPos = if (token.position >= BoardRules.HOME_COL_START) {
            token.position
        } else {
            (token.position - startPos + BoardRules.TRACK_SIZE) % BoardRules.TRACK_SIZE
        }

        val newRelPos = relPos + diceValue

        return when {
            newRelPos > BoardRules.FINISH_POSITION -> token // Overshoot
            newRelPos == BoardRules.FINISH_POSITION ->
                token.copy(position = BoardRules.FINISH_POSITION, status = TokenStatus.FINISHED)
            newRelPos >= BoardRules.HOME_COL_START ->
                token.copy(position = newRelPos)
            else ->
                token.copy(position = (startPos + newRelPos) % BoardRules.TRACK_SIZE)
        }
    }
}
