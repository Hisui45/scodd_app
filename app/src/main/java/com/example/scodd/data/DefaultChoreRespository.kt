package com.example.scodd.data

import com.example.scodd.data.source.local.ChoreDao
import com.example.scodd.data.source.network.NetworkDataSource
import com.example.scodd.di.ApplicationScope
import com.example.scodd.di.DefaultDispatcher
import com.example.scodd.model.Chore
import com.example.scodd.model.Room
import com.example.scodd.model.RoutineInfo
import com.example.scodd.model.ScoddTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultChoreRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: ChoreDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : ChoreRepository {
    override suspend fun createChore(
        title: String,
        rooms: List<Room>,
        workflows: List<String>,
        routineInfo: RoutineInfo,
        isTimeModeActive: Boolean,
        timerValue: Int,
        timerOption: ScoddTime,
        isBankModeActive: Boolean,
        bankValue: Int,
        isFavorite: Boolean
    ): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val choreId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val chore = Chore(
            title = title,
            id = choreId,
            rooms = rooms,
            workflows = workflows,
            routineInfo = routineInfo,
            isTimeModeActive = isTimeModeActive,
            timerModeValue = timerValue,
            timerOption = timerOption,
            isBankModeActive = isBankModeActive,
            bankModeValue = bankValue,
            isFavorite = isFavorite
        )
        localDataSource.upsertChore(chore.toLocalChore())
        saveChoresToNetwork()
        return choreId
    }

    override suspend fun updateChore(
        choreId: String,
        title: String,
        rooms: List<Room>,
        workflows: List<String>,
        routineInfo: RoutineInfo,
        isTimeModeActive: Boolean,
        timerModeValue: Int,
        timerOption: ScoddTime,
        isBankModeActive: Boolean,
        bankModeValue: Int,
        isFavorite: Boolean
    ) {
        val chore = getChore(choreId)?.copy(
            title = title,
            rooms = rooms,
            workflows = workflows,
            routineInfo = routineInfo,
            isTimeModeActive = isTimeModeActive,
            timerModeValue = timerModeValue,
            timerOption = timerOption,
            isBankModeActive = isBankModeActive,
            bankModeValue = bankModeValue,
            isFavorite = isFavorite
        ) ?: throw Exception("Chore (id $choreId) not found")

        localDataSource.upsertChore(chore.toLocalChore())
        saveChoresToNetwork()
    }

    override suspend fun toggleRoom(roomId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getChores(forceUpdate: Boolean): List<Chore> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAllChores().toExternalChore()
        }
    }

    override suspend fun getRooms(forceUpdate: Boolean): List<Room> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAllRooms().toExternalRoom()
        }
    }



    override fun getChoresStream(): Flow<List<Chore>> {
        return localDataSource.observeAllChores().map { chores ->
            withContext(dispatcher) {
                chores.toExternalChore()
            }
        }
    }

    override fun getRoomsStream(): Flow<List<Room>> {
        return localDataSource.observeAllRooms().map { room ->
            withContext(dispatcher) {
                room.toExternalRoom()
            }
        }
    }

    override suspend fun refreshChore(choreId: String) {
        refresh()
    }

    override fun getChoreStream(choreId: String): Flow<Chore?> {
        return localDataSource.observeChoreById(choreId).map { it.toExternalChore() }
    }

    /**
     * Get a Task with the given ID. Will return null if the chore cannot be found.
     *
     * @param choreId - The ID of the chore
     * @param forceUpdate - true if the chore should be updated from the network data source first.
     */
    override suspend fun getChore(choreId: String, forceUpdate: Boolean): Chore? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getChoreById(choreId)?.toExternalChore()
    }

    override suspend fun favoriteChore(choreId: String) {
        localDataSource.updateFavorite(choreId = choreId, favorite = true)
        saveChoresToNetwork()
    }

    override suspend fun unFavoriteChore(choreId: String) {
        localDataSource.updateFavorite(choreId = choreId, favorite = false)
        saveChoresToNetwork()
    }

//    override suspend fun activateTask(choreId: String) {
//        localDataSource.updateCompleted(choreId = choreId, completed = false)
//        saveChoresToNetwork()
//    }

//    override suspend fun clearCompletedChores() {
//        localDataSource.deleteCompleted()
//        saveChoresToNetwork()
//    }

    override suspend fun deleteAllChores() {
        localDataSource.deleteAllChores()
        saveChoresToNetwork()
    }

    override suspend fun deleteChore(choreId: String) {
        localDataSource.deleteChoreById(choreId)
        saveChoresToNetwork()
    }

    /**
     * The following methods load chores from (refresh), and save chores to, the network.
     *
     * Real apps may want to do a proper sync, rather than the "one-way sync everything" approach
     * below. See https://developer.android.com/topic/architecture/data-layer/offline-first
     * for more efficient and robust synchronisation strategies.
     *
     * Note that the refresh operation is a suspend function (forces callers to wait) and the save
     * operation is not. It returns immediately so callers don't have to wait.
     */

    /**
     * Delete everything in the local data source and replace it with everything from the network
     * data source.
     *
     * `withContext` is used here in case the bulk `toLocal` mapping operation is complex.
     */
    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteChores = networkDataSource.loadChores()
            localDataSource.deleteAllChores()
            localDataSource.upsertAllChores(remoteChores.toLocalChore())
        }
    }

    /**
     * Send the chores from the local data source to the network data source
     *
     * Returns immediately after launching the job. Real apps may want to suspend here until the
     * operation is complete or (better) use WorkManager to schedule this work. Both approaches
     * should provide a mechanism for failures to be communicated back to the user so that
     * they are aware that their data isn't being backed up.
     */
    private fun saveChoresToNetwork() {
        scope.launch {
            try {
                val localChores = localDataSource.getAllChores()
                val networkChores = withContext(dispatcher) {
                    localChores.toNetworkChore()
                }
                networkDataSource.saveChores(networkChores)
            } catch (e: Exception) {
                // In a real app you'd handle the exception e.g. by exposing a `networkStatus` flow
                // to an app level UI state holder which could then display a Toast message.
            }
        }
    }
}