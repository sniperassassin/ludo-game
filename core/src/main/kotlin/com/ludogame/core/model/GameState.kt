package com.ludogame.core.model

enum class GamePhase { WAITING, ROLLING, MOVING, FINISHED }

data class GameState(
    val roomId: String = "",
    val players: List<Player> = emptyList(),
    val currentTurnIndex: Int = 0,
    val diceValue: Int = 0,
    val consecutiveSixes: Int = 0,
    val phase: GamePhase = GamePhase.WAITING,
    val winnerId: String? = null
)
