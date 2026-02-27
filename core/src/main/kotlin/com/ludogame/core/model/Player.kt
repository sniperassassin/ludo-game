package com.ludogame.core.model

enum class PlayerColor { RED, BLUE, GREEN, YELLOW }

data class Player(
    val id: String,
    val name: String,
    val color: PlayerColor,
    val tokens: List<Token> = List(4) { i -> Token(id = i, playerId = id) }
)
