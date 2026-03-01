package com.ludogame.app.ui.board

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.ludogame.app.ui.theme.LudoBlue
import com.ludogame.app.ui.theme.LudoGreen
import com.ludogame.app.ui.theme.LudoRed
import com.ludogame.app.ui.theme.LudoYellow
import com.ludogame.core.model.GameState
import com.ludogame.core.model.Player
import com.ludogame.core.model.PlayerColor
import com.ludogame.core.model.Token
import com.ludogame.core.model.TokenStatus
import kotlin.math.cos
import kotlin.math.sin

private val playerColors = mapOf(
    PlayerColor.RED    to LudoRed,
    PlayerColor.BLUE   to LudoBlue,
    PlayerColor.GREEN  to LudoGreen,
    PlayerColor.YELLOW to LudoYellow,
)

@Composable
fun LudoBoard(
    gameState: GameState,
    movableTokenIds: List<Int>,
    onTokenClick: (tokenId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(gameState, movableTokenIds) {
                detectTapGestures { offset ->
                    val cellSize = size.width / 15f
                    handleTap(offset, cellSize, gameState, movableTokenIds, onTokenClick)
                }
            }
    ) {
        val s = size.width / 15f

        drawBoard(s)
        drawTokens(gameState, movableTokenIds, s)
    }
}

private fun DrawScope.drawBoard(s: Float) {
    // 1. Gray background
    drawRect(Color(0xFF9E9E9E))

    // 2. Home bases (6x6 colored corners)
    drawRect(LudoRed,    topLeft = Offset(0f,    0f),    size = Size(s * 6, s * 6))
    drawRect(LudoBlue,   topLeft = Offset(s * 9, 0f),    size = Size(s * 6, s * 6))
    drawRect(LudoGreen,  topLeft = Offset(s * 9, s * 9), size = Size(s * 6, s * 6))
    drawRect(LudoYellow, topLeft = Offset(0f,    s * 9), size = Size(s * 6, s * 6))

    // Inner parking areas (white circle area inside each home base)
    val parkingAlpha = 0.35f
    drawRect(Color.White.copy(alpha = parkingAlpha), Offset(s * 1, s * 1), Size(s * 4, s * 4))
    drawRect(Color.White.copy(alpha = parkingAlpha), Offset(s * 10, s * 1), Size(s * 4, s * 4))
    drawRect(Color.White.copy(alpha = parkingAlpha), Offset(s * 10, s * 10), Size(s * 4, s * 4))
    drawRect(Color.White.copy(alpha = parkingAlpha), Offset(s * 1, s * 10), Size(s * 4, s * 4))

    // 3. White cross (the track path area)
    drawRect(Color.White, topLeft = Offset(0f,    s * 6), size = Size(s * 15, s * 3)) // horizontal
    drawRect(Color.White, topLeft = Offset(s * 6, 0f),    size = Size(s * 3, s * 15)) // vertical

    // 4. Colored home columns (leading into the center)
    BoardData.homeColumns.forEach { (color, cells) ->
        val fill = playerColors[color]!!.copy(alpha = 0.55f)
        cells.forEach { pos ->
            drawRect(fill, Offset(pos.col * s, pos.row * s), Size(s, s))
        }
    }

    // 5. Center finish square
    drawRect(Color(0xFFFFF176), Offset(s * 6, s * 6), Size(s * 3, s * 3))
    drawCircle(
        color  = Color(0xFFFFD700),
        radius = s * 0.9f,
        center = Offset(s * 7.5f, s * 7.5f)
    )
    drawCircle(
        color       = Color.White.copy(alpha = 0.6f),
        radius      = s * 0.55f,
        center      = Offset(s * 7.5f, s * 7.5f)
    )

    // 6. Highlight safe squares (star squares)
    BoardData.safeGridPositions.forEach { pos ->
        drawRect(
            Color(0xFFFFC107).copy(alpha = 0.35f),
            Offset(pos.col * s, pos.row * s),
            Size(s, s)
        )
        
        val center = Offset(pos.col * s + s / 2f, pos.row * s + s / 2f)
        drawStar(center, s * 0.35f, Color(0xFFFFC107).copy(alpha = 0.8f))
    }

    // 7. Colored tint on each color's starting square
    BoardData.startGridPositions.forEach { (color, pos) ->
        drawRect(
            playerColors[color]!!.copy(alpha = 0.6f),
            Offset(pos.col * s, pos.row * s),
            Size(s, s)
        )
    }

    // 8. Grid lines
    val gridColor = Color.Black.copy(alpha = 0.12f)
    for (i in 0..15) {
        drawLine(gridColor, Offset(i * s, s * 6), Offset(i * s, s * 9), strokeWidth = 0.8f)
        drawLine(gridColor, Offset(s * 6, i * s), Offset(s * 9, i * s), strokeWidth = 0.8f)
    }
    for (i in 6..9) {
        drawLine(gridColor, Offset(0f, i * s), Offset(s * 15, i * s), strokeWidth = 0.8f)
        drawLine(gridColor, Offset(i * s, 0f), Offset(i * s, s * 15), strokeWidth = 0.8f)
    }
}

private fun DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
    val path = Path().apply {
        val angleStep = (Math.PI / 5).toFloat()
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) radius else radius * 0.45f
            val angle = i * angleStep - (Math.PI / 2).toFloat()
            val x = center.x + r * cos(angle.toDouble()).toFloat()
            val y = center.y + r * sin(angle.toDouble()).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawTokens(
    gameState: GameState,
    movableTokenIds: List<Int>,
    s: Float
) {
    val currentPlayer = gameState.players.getOrNull(gameState.currentTurnIndex)
    
    // Group tokens by their grid position to handle stacking
    val tokensByPos = mutableMapOf<GridPos, MutableList<Pair<Token, PlayerColor>>>()
    
    gameState.players.forEach { player ->
        player.tokens.forEach { token ->
            val pos = tokenGridPos(token, player)
            if (pos != null) {
                tokensByPos.getOrPut(pos) { mutableListOf() }.add(token to player.color)
            }
        }
    }

    tokensByPos.forEach { (pos, tokensAtPos) ->
        val center = Offset(pos.col * s + s / 2f, pos.row * s + s / 2f)
        val baseRadius = s * 0.32f
        
        if (tokensAtPos.size > 1) {
            // Stacked tokens
            tokensAtPos.forEachIndexed { index, (token, playerColor) ->
                val color = playerColors[playerColor] ?: Color.Gray
                // ONLY highlight if it is the current player's turn AND the token is in movableTokenIds
                val isCurrentPlayerToken = currentPlayer?.color == playerColor
                val isMovable = isCurrentPlayerToken && token.id in movableTokenIds
                
                val offsetX = (index - (tokensAtPos.size - 1) / 2f) * (s * 0.12f)
                val offsetY = (index - (tokensAtPos.size - 1) / 2f) * (s * 0.08f)
                val tokenCenter = center.copy(x = center.x + offsetX, y = center.y + offsetY)
                
                drawTokenAt(tokenCenter, baseRadius * 0.9f, color, isMovable)
            }
        } else {
            val (token, playerColor) = tokensAtPos.first()
            val color = playerColors[playerColor] ?: Color.Gray
            // ONLY highlight if it is the current player's turn AND the token is in movableTokenIds
            val isCurrentPlayerToken = currentPlayer?.color == playerColor
            val isMovable = isCurrentPlayerToken && token.id in movableTokenIds
            
            drawTokenAt(center, baseRadius, color, isMovable)
        }
    }
}

private fun DrawScope.drawTokenAt(center: Offset, radius: Float, color: Color, isMovable: Boolean) {
    // Shadow
    drawCircle(Color.Black.copy(alpha = 0.25f), radius * 1.05f, center.copy(y = center.y + radius * 0.15f))
    // Body
    drawCircle(color, radius, center)
    // Outline
    drawCircle(Color.Black.copy(alpha = 0.6f), radius, center, style = Stroke(1.5f))
    // Specular highlight
    drawCircle(Color.White.copy(alpha = 0.45f), radius * 0.45f,
        center.copy(x = center.x - radius * 0.22f, y = center.y - radius * 0.22f))
    // Movable ring
    if (isMovable) {
        drawCircle(Color.White, radius * 1.45f, center, style = Stroke(2.5f))
    }
}

private fun tokenGridPos(token: Token, player: Player): GridPos? = when {
    token.status == TokenStatus.HOME     -> BoardData.homeParking[player.color]?.getOrNull(token.id)
    token.status == TokenStatus.FINISHED -> BoardData.centerSquare
    token.position >= 52                 -> BoardData.homeColumns[player.color]?.getOrNull(token.position - 52)
    else                                 -> BoardData.trackCoordinates.getOrNull(token.position)
}

private fun handleTap(
    offset: Offset,
    cellSize: Float,
    gameState: GameState,
    movableTokenIds: List<Int>,
    onTokenClick: (Int) -> Unit
) {
    if (movableTokenIds.isEmpty()) return
    val tapRow = (offset.y / cellSize).toInt()
    val tapCol = (offset.x / cellSize).toInt()
    
    val currentPlayer = gameState.players.getOrNull(gameState.currentTurnIndex) ?: return

    // Check only current player's tokens for the click
    currentPlayer.tokens
        .filter { it.id in movableTokenIds }
        .forEach { token ->
            val pos = tokenGridPos(token, currentPlayer) ?: return@forEach
            if (pos.row == tapRow && pos.col == tapCol) {
                onTokenClick(token.id)
                return
            }
        }
}
