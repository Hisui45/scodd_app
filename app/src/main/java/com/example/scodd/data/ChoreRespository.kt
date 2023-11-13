package com.example.scodd.data

import com.example.scodd.model.Chore
import com.example.scodd.model.Room
import com.example.scodd.model.RoutineInfo
import com.example.scodd.model.ScoddTime
import kotlinx.coroutines.flow.Flow

interface ChoreRepository {

    fun getChoresStream(): Flow<List<Chore>>

    fun getRoomsStream(): Flow<List<Room>>

    suspend fun getChores(forceUpdate: Boolean = false): List<Chore>

    suspend fun getRooms(forceUpdate: Boolean = false): List<Room>

    suspend fun refresh()

    fun getChoreStream(choreId: String): Flow<Chore?>

    suspend fun getChore(choreId: String, forceUpdate: Boolean = false): Chore?

    suspend fun refreshChore(choreId: String)

    suspend fun createChore(title: String, rooms: List<Room>, workflows: List<String>, routineInfo: RoutineInfo,
                            isTimeModeActive: Boolean, timerModeValue: Int, timerOption: ScoddTime,
                            isBankModeActive: Boolean, bankModeValue: Int,
                            isFavorite: Boolean): String

    suspend fun updateChore(choreId: String, title: String, rooms: List<Room>, workflows: List<String>, routineInfo: RoutineInfo,
                            isTimeModeActive: Boolean, timerModeValue: Int, timerOption: ScoddTime,
                            isBankModeActive: Boolean, bankModeValue: Int,
                            isFavorite: Boolean)

    suspend fun toggleRoom(roomId: String)

    suspend fun favoriteChore(choreId: String)

    suspend fun unFavoriteChore(choreId: String)

//    suspend fun clearCompletedChores()

    suspend fun deleteAllChores()

    suspend fun deleteChore(choreId: String)
}