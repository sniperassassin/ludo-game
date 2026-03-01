package com.ludogame.app.ui.board

import com.ludogame.core.model.PlayerColor

data class GridPos(val row: Int, val col: Int)

object BoardData {

    /**
     * 52 main track squares in clockwise order.
     * Index 0 is RED start, Index 13 is BLUE start, Index 26 is GREEN start, Index 39 is YELLOW start.
     */
    val trackCoordinates: List<GridPos> = listOf(
        // Left arm, top row (starts at RED entry)
        GridPos(6, 1), GridPos(6, 2), GridPos(6, 3), GridPos(6, 4), GridPos(6, 5),
        // Top arm, left column
        GridPos(5, 6), GridPos(4, 6), GridPos(3, 6), GridPos(2, 6), GridPos(1, 6), GridPos(0, 6),
        // Top arm, middle junction
        GridPos(0, 7),
        // Top arm, right column (starts at BLUE entry)
        GridPos(0, 8), GridPos(1, 8), GridPos(2, 8), GridPos(3, 8), GridPos(4, 8), GridPos(5, 8),
        // Right arm, top row
        GridPos(6, 9), GridPos(6, 10), GridPos(6, 11), GridPos(6, 12), GridPos(6, 13), GridPos(6, 14),
        // Right arm, middle junction
        GridPos(7, 14),
        // Right arm, bottom row (starts at GREEN entry)
        GridPos(8, 14), GridPos(8, 13), GridPos(8, 12), GridPos(8, 11), GridPos(8, 10), GridPos(8, 9),
        // Bottom arm, right column
        GridPos(9, 8), GridPos(10, 8), GridPos(11, 8), GridPos(12, 8), GridPos(13, 8), GridPos(14, 8),
        // Bottom arm, middle junction
        GridPos(14, 7),
        // Bottom arm, left column (starts at YELLOW entry)
        GridPos(14, 6), GridPos(13, 6), GridPos(12, 6), GridPos(11, 6), GridPos(10, 6), GridPos(9, 6),
        // Left arm, bottom row
        GridPos(8, 5), GridPos(8, 4), GridPos(8, 3), GridPos(8, 2), GridPos(8, 1), GridPos(8, 0),
        // Left arm, middle junction
        GridPos(7, 0),
        // Left arm, edge before RED start
        GridPos(6, 0)
    )

    // Colored home column cells leading to center finish (5 squares each)
    val homeColumns: Map<PlayerColor, List<GridPos>> = mapOf(
        PlayerColor.RED    to listOf(GridPos(7, 1), GridPos(7, 2), GridPos(7, 3), GridPos(7, 4), GridPos(7, 5)),
        PlayerColor.BLUE   to listOf(GridPos(1, 7), GridPos(2, 7), GridPos(3, 7), GridPos(4, 7), GridPos(5, 7)),
        PlayerColor.GREEN  to listOf(GridPos(7, 13), GridPos(7, 12), GridPos(7, 11), GridPos(7, 10), GridPos(7, 9)),
        PlayerColor.YELLOW to listOf(GridPos(13, 7), GridPos(12, 7), GridPos(11, 7), GridPos(10, 7), GridPos(9, 7)),
    )

    val centerSquare = GridPos(7, 7)

    // Token parking spots inside each home base (4 per color, indexed by token.id)
    val homeParking: Map<PlayerColor, List<GridPos>> = mapOf(
        PlayerColor.RED    to listOf(GridPos(2, 2),  GridPos(2, 4),  GridPos(4, 2),  GridPos(4, 4)),
        PlayerColor.BLUE   to listOf(GridPos(2, 10), GridPos(2, 12), GridPos(4, 10), GridPos(4, 12)),
        PlayerColor.GREEN  to listOf(GridPos(10, 10),GridPos(10, 12),GridPos(12, 10),GridPos(12, 12)),
        PlayerColor.YELLOW to listOf(GridPos(10, 2), GridPos(10, 4), GridPos(12, 2), GridPos(12, 4)),
    )

    // Grid positions of safe squares (star squares)
    val safeGridPositions: Set<GridPos> = setOf(
        trackCoordinates[0],  trackCoordinates[8],
        trackCoordinates[13], trackCoordinates[21],
        trackCoordinates[26], trackCoordinates[34],
        trackCoordinates[39], trackCoordinates[47],
    )

    // Start square for each color (for rendering a colored tint)
    val startGridPositions: Map<PlayerColor, GridPos> = mapOf(
        PlayerColor.RED    to trackCoordinates[0],
        PlayerColor.BLUE   to trackCoordinates[13],
        PlayerColor.GREEN  to trackCoordinates[26],
        PlayerColor.YELLOW to trackCoordinates[39],
    )
}
