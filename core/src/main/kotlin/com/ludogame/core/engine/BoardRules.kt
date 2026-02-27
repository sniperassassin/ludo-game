package com.ludogame.core.engine

import com.ludogame.core.model.PlayerColor
import com.ludogame.core.model.Token
import com.ludogame.core.model.TokenStatus

object BoardRules {
    const val TRACK_SIZE = 52
    const val TOKENS_PER_PLAYER = 4
    const val DICE_TO_EXIT_HOME = 6
    const val HOME_COL_START  = 52  // extended position: home column step 1
    const val HOME_COL_END    = 56  // extended position: home column step 5
    const val FINISH_POSITION = 57  // extended position: center / finished

    // Entry position on the main track for each color
    val startPositions = mapOf(
        PlayerColor.RED    to 0,
        PlayerColor.BLUE   to 13,
        PlayerColor.GREEN  to 26,
        PlayerColor.YELLOW to 39
    )

    // Squares where a token cannot be captured
    val safeSquares = setOf(0, 8, 13, 21, 26, 34, 39, 47)

    // Last main-track square before a color's home column
    val homeEntryPositions = mapOf(
        PlayerColor.RED    to 50,
        PlayerColor.BLUE   to 11,
        PlayerColor.GREEN  to 24,
        PlayerColor.YELLOW to 37
    )

    fun canExitHome(diceValue: Int): Boolean = diceValue == DICE_TO_EXIT_HOME

    fun isSafe(position: Int): Boolean = position in safeSquares

    fun movableTokens(tokens: List<Token>, diceValue: Int, color: PlayerColor): List<Token> =
        tokens.filter { token ->
            when (token.status) {
                TokenStatus.HOME     -> canExitHome(diceValue)
                TokenStatus.ACTIVE   -> when {
                    // In home column: can't overshoot past center
                    token.position in HOME_COL_START..HOME_COL_END ->
                        token.position + diceValue <= FINISH_POSITION
                    // On main track: max relPos(50) + max dice(6) = 56 → FINISH, never overshoots
                    else -> true
                }
                TokenStatus.FINISHED -> false
            }
        }
}
