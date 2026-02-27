package com.ludogame.core.model

enum class TokenStatus { HOME, ACTIVE, FINISHED }

data class Token(
    val id: Int,
    val playerId: String,
    val position: Int = -1,   // -1 = sitting at home base
    val status: TokenStatus = TokenStatus.HOME
)
