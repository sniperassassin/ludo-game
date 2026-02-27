package com.ludogame.core.engine

import com.ludogame.core.model.GamePhase
import com.ludogame.core.model.Player
import com.ludogame.core.model.PlayerColor
import com.ludogame.core.model.Token
import com.ludogame.core.model.TokenStatus
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class GameEngineTest {

    private lateinit var engine: GameEngine
    private lateinit var diceRoller: DiceRoller
    private lateinit var players: List<Player>

    @Before
    fun setUp() {
        diceRoller = mockk()
        engine = GameEngine(diceRoller)
        players = listOf(
            Player("p1", "Alice", PlayerColor.RED),
            Player("p2", "Bob", PlayerColor.BLUE)
        )
    }

    @Test
    fun `startGame creates state with correct player count`() {
        val state = engine.startGame(players)
        assertEquals(2, state.players.size)
        assertEquals(GamePhase.ROLLING, state.phase)
    }

    @Test
    fun `rolling dice transitions phase to MOVING`() {
        val state = engine.startGame(players)
        every { diceRoller.roll() } returns 4
        val newState = engine.rollDice(state)
        assertEquals(4, newState.diceValue)
        assertEquals(GamePhase.MOVING, newState.phase)
    }

    @Test
    fun `token exits home base on a roll of 6`() {
        every { diceRoller.roll() } returns 6
        val state = engine.startGame(players)
        val rolled = engine.rollDice(state)
        val tokenId = players[0].tokens[0].id
        val result = engine.moveToken(rolled, tokenId)
        val token = result.players[0].tokens.find { it.id == tokenId }
        assertEquals(TokenStatus.ACTIVE, token?.status)
    }

    @Test
    fun `token stays home on non-6 roll`() {
        every { diceRoller.roll() } returns 4
        val state = engine.startGame(players)
        val rolled = engine.rollDice(state)
        val tokenId = players[0].tokens[0].id
        val result = engine.moveToken(rolled, tokenId)
        val token = result.players[0].tokens.find { it.id == tokenId }
        assertEquals(TokenStatus.HOME, token?.status)
    }

    @Test
    fun `turn advances after a non-6 move`() {
        every { diceRoller.roll() } returns 3
        val state = engine.startGame(players)
        val rolled = engine.rollDice(state)
        val result = engine.moveToken(rolled, players[0].tokens[0].id)
        assertEquals(1, result.currentTurnIndex)
    }

    @Test
    fun `player gets extra turn on roll of 6`() {
        every { diceRoller.roll() } returns 6
        val state = engine.startGame(players)
        val rolled = engine.rollDice(state)
        val result = engine.moveToken(rolled, players[0].tokens[0].id)
        assertEquals(0, result.currentTurnIndex)
    }

    @Test
    fun `startGame throws on fewer than 2 players`() {
        assertThrows(IllegalArgumentException::class.java) {
            engine.startGame(listOf(Player("p1", "Alice", PlayerColor.RED)))
        }
    }

    @Test
    fun `startGame throws on more than 4 players`() {
        val tooMany = (1..5).map { Player("p$it", "P$it", PlayerColor.RED) }
        assertThrows(IllegalArgumentException::class.java) {
            engine.startGame(tooMany)
        }
    }

    // --- Home column & finishing ---

    @Test
    fun `token enters home column when relPos crosses 51`() {
        // RED starts at 0; a token at position 49 (relPos 49) + dice 3 = relPos 52 = HOME_COL_START
        every { diceRoller.roll() } returns 3
        val redWithActiveToken = players[0].copy(
            tokens = listOf(Token(id = 0, playerId = "p1", position = 49, status = TokenStatus.ACTIVE))
                    + players[0].tokens.drop(1)
        )
        val state = engine.startGame(listOf(redWithActiveToken, players[1]))
            .copy(phase = GamePhase.MOVING, diceValue = 3)
        val result = engine.moveToken(state, 0)
        val token = result.players[0].tokens.find { it.id == 0 }
        assertEquals(BoardRules.HOME_COL_START, token?.position)
        assertEquals(TokenStatus.ACTIVE, token?.status)
    }

    @Test
    fun `token finishes when relPos reaches 57`() {
        // RED token at position 51 (relPos 51) + dice 6 = relPos 57 = FINISHED
        every { diceRoller.roll() } returns 6
        val redWithActiveToken = players[0].copy(
            tokens = listOf(Token(id = 0, playerId = "p1", position = 51, status = TokenStatus.ACTIVE))
                    + players[0].tokens.drop(1)
        )
        val state = engine.startGame(listOf(redWithActiveToken, players[1]))
            .copy(phase = GamePhase.MOVING, diceValue = 6)
        val result = engine.moveToken(state, 0)
        val token = result.players[0].tokens.find { it.id == 0 }
        assertEquals(TokenStatus.FINISHED, token?.status)
    }

    @Test
    fun `token finishes from home column when reaching position 57`() {
        // RED token at home column position 56 (step 5) + dice 1 = 57 = FINISHED
        every { diceRoller.roll() } returns 1
        val redWithHomeColToken = players[0].copy(
            tokens = listOf(Token(id = 0, playerId = "p1", position = 56, status = TokenStatus.ACTIVE))
                    + players[0].tokens.drop(1)
        )
        val state = engine.startGame(listOf(redWithHomeColToken, players[1]))
            .copy(phase = GamePhase.MOVING, diceValue = 1)
        val result = engine.moveToken(state, 0)
        val token = result.players[0].tokens.find { it.id == 0 }
        assertEquals(TokenStatus.FINISHED, token?.status)
    }

    @Test
    fun `home column token cannot overshoot`() {
        // RED token at position 56 (step 5) + dice 2 would overshoot; should not be movable
        val redWithHomeColToken = players[0].copy(
            tokens = listOf(Token(id = 0, playerId = "p1", position = 56, status = TokenStatus.ACTIVE))
                    + players[0].tokens.drop(1)
        )
        val movable = BoardRules.movableTokens(redWithHomeColToken.tokens, diceValue = 2, color = PlayerColor.RED)
        assertEquals(0, movable.size)
    }

    // --- Capture ---

    @Test
    fun `landing on opponent token on non-safe square sends it home`() {
        every { diceRoller.roll() } returns 3
        // RED token at position 1, BLUE token at position 4 (non-safe)
        val red = players[0].copy(
            tokens = listOf(Token(id = 0, playerId = "p1", position = 1, status = TokenStatus.ACTIVE))
                    + players[0].tokens.drop(1)
        )
        val blue = players[1].copy(
            tokens = listOf(Token(id = 0, playerId = "p2", position = 4, status = TokenStatus.ACTIVE))
                    + players[1].tokens.drop(1)
        )
        val state = engine.startGame(listOf(red, blue))
            .copy(phase = GamePhase.MOVING, diceValue = 3)
        val result = engine.moveToken(state, 0)
        val blueToken = result.players[1].tokens.find { it.id == 0 }
        assertEquals(TokenStatus.HOME, blueToken?.status)
    }

    @Test
    fun `landing on safe square does not capture opponent`() {
        every { diceRoller.roll() } returns 7
        // RED token at position 1, BLUE token at position 8 (safe square)
        val red = players[0].copy(
            tokens = listOf(Token(id = 0, playerId = "p1", position = 1, status = TokenStatus.ACTIVE))
                    + players[0].tokens.drop(1)
        )
        val blue = players[1].copy(
            tokens = listOf(Token(id = 0, playerId = "p2", position = 8, status = TokenStatus.ACTIVE))
                    + players[1].tokens.drop(1)
        )
        val state = engine.startGame(listOf(red, blue))
            .copy(phase = GamePhase.MOVING, diceValue = 7)
        val result = engine.moveToken(state, 0)
        val blueToken = result.players[1].tokens.find { it.id == 0 }
        assertEquals(TokenStatus.ACTIVE, blueToken?.status)
    }
}
