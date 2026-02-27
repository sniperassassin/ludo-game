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
        return state.copy(diceValue = value, phase = GamePhase.MOVING)
    }

    fun moveToken(state: GameState, tokenId: Int): GameState {
        require(state.phase == GamePhase.MOVING) { "Not in moving phase" }
        val currentPlayer = state.players[state.currentTurnIndex]
        val token = currentPlayer.tokens.find { it.id == tokenId } ?: return state

        val updatedToken = applyMove(token, state.diceValue, currentPlayer)
        val updatedPlayer = currentPlayer.copy(
            tokens = currentPlayer.tokens.map { if (it.id == tokenId) updatedToken else it }
        )
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
                else p.copy(
                    tokens = p.tokens.map { t ->
                        if (t.status == TokenStatus.ACTIVE && t.position == updatedToken.position)
                            t.copy(position = -1, status = TokenStatus.HOME)
                        else t
                    }
                )
            }
        }

        val winner = updatedPlayers.find { p -> p.tokens.all { it.status == TokenStatus.FINISHED } }

        // Rolling a 6 grants an extra turn
        val nextTurnIndex = if (state.diceValue == 6) state.currentTurnIndex
                            else (state.currentTurnIndex + 1) % state.players.size

        return state.copy(
            players = updatedPlayers,
            currentTurnIndex = nextTurnIndex,
            phase = if (winner != null) GamePhase.FINISHED else GamePhase.ROLLING,
            winnerId = winner?.id
        )
    }

    private fun applyMove(token: Token, diceValue: Int, player: Player): Token =
        when (token.status) {
            TokenStatus.HOME -> {
                if (BoardRules.canExitHome(diceValue)) {
                    val startPos = BoardRules.startPositions[player.color] ?: 0
                    token.copy(position = startPos, status = TokenStatus.ACTIVE)
                } else token
            }
            TokenStatus.ACTIVE -> {
                val startPos = BoardRules.startPositions[player.color] ?: return token
                when {
                    // Already in home column (pos 52–56): move straight toward center
                    token.position in BoardRules.HOME_COL_START..BoardRules.HOME_COL_END -> {
                        val newPos = token.position + diceValue
                        if (newPos == BoardRules.FINISH_POSITION)
                            token.copy(position = newPos, status = TokenStatus.FINISHED)
                        else
                            token.copy(position = newPos)
                    }
                    // On main track: compute relative position from start
                    else -> {
                        val relPos = (token.position - startPos + BoardRules.TRACK_SIZE) % BoardRules.TRACK_SIZE
                        val newRelPos = relPos + diceValue
                        when {
                            newRelPos <= 50 ->
                                token.copy(position = (startPos + newRelPos) % BoardRules.TRACK_SIZE)
                            newRelPos + 1 in BoardRules.HOME_COL_START..BoardRules.HOME_COL_END ->
                                token.copy(position = newRelPos + 1)  // enters home column (52–56)
                            else ->
                                token.copy(position = BoardRules.FINISH_POSITION, status = TokenStatus.FINISHED)
                        }
                    }
                }
            }
            TokenStatus.FINISHED -> token
        }
}
