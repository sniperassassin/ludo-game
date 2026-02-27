package com.ludogame.network.repository

import com.ludogame.core.model.GameState
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    suspend fun createRoom(playerName: String): String          // returns roomId
    suspend fun joinRoom(roomId: String, playerName: String): Boolean
    fun observeGameState(roomId: String): Flow<GameState>
    suspend fun updateGameState(roomId: String, state: GameState)
    suspend fun leaveRoom(roomId: String)
}
