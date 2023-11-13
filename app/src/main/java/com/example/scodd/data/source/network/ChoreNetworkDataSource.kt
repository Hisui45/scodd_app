package com.example.scodd.data.source.network

import com.example.scodd.model.Room
import com.example.scodd.model.RoutineInfo
import com.example.scodd.model.ScoddTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class ChoreNetworkDataSource @Inject constructor() : NetworkDataSource {

    // A mutex is used to ensure that reads and writes are thread-safe.
    private val accessMutex = Mutex()
    private var chores = listOf(
        NetworkChore(
            id = "5S",
            title = "Clean Toilet",
            rooms = emptyList(),
            routineInfo = RoutineInfo(),
            isBankModeActive = false,
            timerModeValue = 4,
            isTimeModeActive = false,
            timerOption = ScoddTime.MINUTE,
            bankModeValue = 8,
            isFavorite = false,
        ),

        NetworkChore(
            id = "6S",
            title = "Brush Teeth",
            rooms =  emptyList(),
            routineInfo = RoutineInfo(),
            isBankModeActive = false,
            timerModeValue = 4,
            isTimeModeActive = false,
            timerOption = ScoddTime.MINUTE,
            bankModeValue = 8,
            isFavorite = false,
        )

    )

    private var rooms = listOf(
        NetworkRoom(id ="1", "Kitchen", false),
        NetworkRoom(id ="2", "Bedroom", false)
    )

    private var workflows = listOf(
        NetworkWorkflow(
            id = "1",
            title = "Quick Room Clean",
            chores = emptyList(),
            routineInfo = RoutineInfo()
        ),
        NetworkWorkflow(
            id = "2",
            title = "Deep Room Clean",
            chores = emptyList(),
            routineInfo = RoutineInfo()
        )
    )

    override suspend fun loadChores(): List<NetworkChore> = accessMutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        return chores
    }

    override suspend fun saveChores(newChores: List<NetworkChore>) = accessMutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        chores = newChores
    }

    override suspend fun loadRooms(): List<NetworkRoom> = accessMutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        return rooms
    }

    override suspend fun saveRooms(newRooms: List<NetworkRoom>) = accessMutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        rooms = newRooms
    }

    override suspend fun loadWorkflows(): List<NetworkWorkflow> = accessMutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        return workflows
    }

    override suspend fun saveWorkflows(newWorkflows: List<NetworkWorkflow>) = accessMutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        workflows = newWorkflows
    }
}

private const val SERVICE_LATENCY_IN_MILLIS = 2000L