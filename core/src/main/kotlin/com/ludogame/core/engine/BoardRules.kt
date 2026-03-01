package com.ludogame.core.engine

import com.ludogame.core.model.PlayerColor
import com.ludogame.core.model.Token
import com.ludogame.core.model.TokenStatus

object BoardRules {
    const val TRACK_SIZE = 52
    const val TOKENS_PER_PLAYER = 4
    const val DICE_TO_EXIT_HOME = 6

    // Relative positions:
    // 0 to 51: Main Track (52 squares)
    // 52 to 56: Home Column (5 squares)
    // 57: Finish
    const val HOME_COL_START  = 52
    const val HOME_COL_END    = 56
    const val FINISH_POSITION = 57

    // Entry position on the main track for each color
    val startPositions = mapOf(
        PlayerColor.RED    to 0,
        PlayerColor.BLUE   to 13,
        PlayerColor.GREEN  to 26,
        PlayerColor.YELLOW to 39
    )

    // Absolute squares where a token cannot be captured
    val safeSquares = setOf(0, 8, 13, 21, 26, 34, 39, 47)

    fun canExitHome(diceValue: Int): Boolean = diceValue == DICE_TO_EXIT_HOME

    fun isSafe(position: Int): Boolean = position in safeSquares

    fun movableTokens(tokens: List<Token>, diceValue: Int, color: PlayerColor): List<Token> =
        tokens.filter { token ->
            when (token.status) {
                TokenStatus.HOME     -> canExitHome(diceValue)
                TokenStatus.ACTIVE   -> {
                    val startPos = startPositions[color] ?: 0
                    val relPos = if (token.position >= HOME_COL_START) {
                        token.position
                    } else {
                        (token.position - startPos + TRACK_SIZE) % TRACK_SIZE
                    }
                    relPos + diceValue <= FINISH_POSITION
                }
                TokenStatus.FINISHED -> false
            }
        }
}
