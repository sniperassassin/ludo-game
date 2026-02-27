package com.ludogame.app.ui.board

import com.ludogame.core.model.PlayerColor

data class GridPos(val row: Int, val col: Int)

object BoardData {

    // 52 main track squares (clockwise, RED starts at pos 0)
    val trackCoordinates: List<GridPos> = listOf(
        GridPos( 6,  1), //  0  RED start, SAFE
        GridPos( 7,  1), //  1
        GridPos( 8,  1), //  2
        GridPos( 9,  1), //  3
        GridPos(10,  1), //  4
        GridPos(11,  1), //  5
        GridPos(12,  1), //  6
        GridPos(13,  1), //  7
        GridPos(13,  2), //  8  SAFE
        GridPos(13,  3), //  9
        GridPos(13,  4), // 10
        GridPos(13,  5), // 11
        GridPos(13,  6), // 12
        GridPos(13,  7), // 13  BLUE start, SAFE
        GridPos(13,  8), // 14
        GridPos(13,  9), // 15
        GridPos(13, 10), // 16
        GridPos(13, 11), // 17
        GridPos(13, 12), // 18
        GridPos(13, 13), // 19
        GridPos(12, 13), // 20
        GridPos(11, 13), // 21  SAFE
        GridPos(10, 13), // 22
        GridPos( 9, 13), // 23
        GridPos( 8, 13), // 24
        GridPos( 7, 13), // 25
        GridPos( 6, 13), // 26  GREEN start, SAFE
        GridPos( 5, 13), // 27
        GridPos( 4, 13), // 28
        GridPos( 3, 13), // 29
        GridPos( 2, 13), // 30
        GridPos( 1, 13), // 31
        GridPos( 1, 12), // 32
        GridPos( 1, 11), // 33
        GridPos( 1, 10), // 34  SAFE
        GridPos( 1,  9), // 35
        GridPos( 1,  8), // 36
        GridPos( 1,  7), // 37
        GridPos( 1,  6), // 38
        GridPos( 1,  5), // 39  YELLOW start, SAFE
        GridPos( 1,  4), // 40
        GridPos( 1,  3), // 41
        GridPos( 1,  2), // 42
        GridPos( 1,  1), // 43
        GridPos( 2,  1), // 44
        GridPos( 3,  1), // 45
        GridPos( 4,  1), // 46
        GridPos( 5,  1), // 47  SAFE
        GridPos( 6,  1), // 48  same cell as pos 0 (wrap-around)
        GridPos( 7,  1), // 49  same cell as pos 1
        GridPos( 8,  1), // 50  RED homeEntry, same cell as pos 2
        GridPos( 9,  1), // 51  same cell as pos 3
    )

    // Colored home column cells leading to center finish (5 squares each)
    val homeColumns: Map<PlayerColor, List<GridPos>> = mapOf(
        PlayerColor.RED    to listOf(GridPos(7,2), GridPos(7,3), GridPos(7,4), GridPos(7,5), GridPos(7,6)),
        PlayerColor.BLUE   to listOf(GridPos(12,7), GridPos(11,7), GridPos(10,7), GridPos(9,7), GridPos(8,7)),
        PlayerColor.GREEN  to listOf(GridPos(7,12), GridPos(7,11), GridPos(7,10), GridPos(7,9), GridPos(7,8)),
        PlayerColor.YELLOW to listOf(GridPos(2,7), GridPos(3,7), GridPos(4,7), GridPos(5,7), GridPos(6,7)),
    )

    val centerSquare = GridPos(7, 7)

    // Token parking spots inside each home base (4 per color, indexed by token.id)
    val homeParking: Map<PlayerColor, List<GridPos>> = mapOf(
        PlayerColor.RED    to listOf(GridPos(2,2),  GridPos(2,4),  GridPos(4,2),  GridPos(4,4)),
        PlayerColor.BLUE   to listOf(GridPos(2,10), GridPos(2,12), GridPos(4,10), GridPos(4,12)),
        PlayerColor.GREEN  to listOf(GridPos(10,10),GridPos(10,12),GridPos(12,10),GridPos(12,12)),
        PlayerColor.YELLOW to listOf(GridPos(10,2), GridPos(10,4), GridPos(12,2), GridPos(12,4)),
    )

    // Grid positions of safe squares (for rendering a highlight)
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
