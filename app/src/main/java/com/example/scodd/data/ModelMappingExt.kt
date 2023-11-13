package com.example.scodd.data

import com.example.scodd.data.source.local.LocalChore
import com.example.scodd.data.source.local.LocalRoom
import com.example.scodd.data.source.local.LocalWorkflow
import com.example.scodd.data.source.network.NetworkChore
import com.example.scodd.data.source.network.NetworkRoom
import com.example.scodd.data.source.network.NetworkWorkflow
import com.example.scodd.model.Chore
import com.example.scodd.model.Room
import com.example.scodd.model.Workflow

/**
 * Data model mapping extension functions. There are three model types:
 *
 * - Task: External model exposed to other layers in the architecture.
 * Obtained using `toExternal`.
 *
 * - NetworkTask: Internal model used to represent a task from the network. Obtained using
 * `toNetwork`.
 *
 * - LocalTask: Internal model used to represent a task stored locally in a database. Obtained
 * using `toLocal`.
 *
 */



/**
 * Chore
 */
// External to local
fun Chore.toLocalChore() = LocalChore(
    id = id,
    title = title,
    rooms = rooms,
//    workflows = workflows,
    routineInfo = routineInfo,
    isTimeModeActive = isTimeModeActive,
    timerModeValue = timerModeValue,
    timerOption = timerOption,
    isBankModeActive = isBankModeActive,
    bankModeValue = bankModeValue,
    isFavorite = isFavorite
)

fun List<Chore>.toLocalChore() = map(Chore::toLocalChore)

// Local to External
fun LocalChore.toExternalChore() = Chore(
    id = id,
    title = title,
    rooms = rooms,
//    workflows = workflows,
    routineInfo = routineInfo,
    isTimeModeActive = isTimeModeActive,
    timerModeValue = timerModeValue,
    timerOption = timerOption,
    isBankModeActive = isBankModeActive,
    bankModeValue = bankModeValue,
    isFavorite = isFavorite
)

// Note: JvmName is used to provide a unique name for each extension function with the same name.
// Without this, type erasure will cause compiler errors because these methods will have the same
// signature on the JVM.
@JvmName("localChoreToExternalChore")
fun List<LocalChore>.toExternalChore() = map(LocalChore::toExternalChore)

// Network to Local
fun NetworkChore.toLocalChore() = LocalChore(
    id = id,
    title = title,
    rooms = rooms,
//    workflows = workflows,
    routineInfo = routineInfo,
    isTimeModeActive = isTimeModeActive,
    timerModeValue = timerModeValue,
    timerOption = timerOption,
    isBankModeActive = isBankModeActive,
    bankModeValue = bankModeValue,
    isFavorite = isFavorite
)

@JvmName("networkChoreToLocalChore")
fun List<NetworkChore>.toLocalChore() = map(NetworkChore::toLocalChore)

// External to Network
fun Chore.toNetworkChore() = toLocalChore().toNetworkChore()

@JvmName("externalChoreToNetworkChore")
fun List<Chore>.toNetworkChore() = map(Chore::toNetworkChore)

// Local to Network

//Chore
fun LocalChore.toNetworkChore() = NetworkChore(
    id = id,
    title = title,
    rooms = rooms,
//    workflows = workflows,
    routineInfo = routineInfo,
    isTimeModeActive = isTimeModeActive,
    timerModeValue = timerModeValue,
    timerOption = timerOption,
    isBankModeActive = isBankModeActive,
    bankModeValue = bankModeValue,
    isFavorite = isFavorite
)

fun List<LocalChore>.toNetworkChore() = map(LocalChore::toNetworkChore)

// Network to External
/**
 * Chore
 */
fun NetworkChore.toExternalChore() = toLocalChore().toExternalChore()

@JvmName("networkChoreToExternalChore")
fun List<NetworkChore>.toExternalChore() = map(NetworkChore::toExternalChore)


/**
 * Room
 */

// External to local
fun Room.toLocalRoom() = LocalRoom(
    id = id,
    title = title,
    selected = selected
)

fun List<Room>.toLocalRoom() = map(Room::toLocalRoom)

//Local to External
fun LocalRoom.toExternalRoom() = Room(
    id = id,
    title = title
)

// Note: JvmName is used to provide a unique name for each extension function with the same name.
// Without this, type erasure will cause compiler errors because these methods will have the same
// signature on the JVM.
@JvmName("localRoomToExternalRoom")
fun List<LocalRoom>.toExternalRoom() = map(LocalRoom::toExternalRoom)

// Network to Local
fun NetworkRoom.toLocalRoom() = LocalRoom(
    id = id,
    title = title,
    selected = selected
)

@JvmName("networkRoomToLocalRoom")
fun List<NetworkRoom>.toLocalRoom() = map(NetworkRoom::toLocalRoom)

// Local to Network
fun LocalRoom.toNetworkRoom() = NetworkRoom(
    id = id,
    title = title,
    selected = selected
)

fun List<LocalRoom>.toNetworkRoom() = map(LocalRoom::toNetworkRoom)

// External to Network
fun Room.toNetworkRoom() = toLocalRoom().toNetworkRoom()

@JvmName("externalRoomToNetworkRoom")
fun List<Room>.toNetworkRoom() = map(Room::toNetworkRoom)

// Network to External
fun NetworkRoom.toExternalRoom() = toLocalRoom().toExternalRoom()

@JvmName("networkRoomToExternalRoom")
fun List<NetworkRoom>.toExternalRoom() = map(NetworkRoom::toExternalRoom)

/**
 * Workflow
 */

// External to local
fun Workflow.toLocalWorkflow() = LocalWorkflow(
    id = id,
    title = title,
    chores = chores,
    routineInfo = routineInfo
)

fun List<Workflow>.toLocalWorkflow() = map(Workflow::toLocalWorkflow)

//Local to External
fun LocalWorkflow.toExternalWorkflow() = Workflow(
    id = id,
    title = title,
    chores = chores,
    routineInfo = routineInfo
)

// Note: JvmName is used to provide a unique name for each extension function with the same name.
// Without this, type erasure will cause compiler errors because these methods will have the same
// signature on the JVM.
@JvmName("localWorkflowToExternalWorkflow")
fun List<LocalWorkflow>.toExternalWorkflow() = map(LocalWorkflow::toExternalWorkflow)

// Network to Local
fun NetworkWorkflow.toLocalWorkflow() = LocalWorkflow(
    id = id,
    title = title,
    chores = chores,
    routineInfo = routineInfo
)

@JvmName("networkWorkflowToLocalWorkflow")
fun List<NetworkWorkflow>.toLocalWorkflow() = map(NetworkWorkflow::toLocalWorkflow)

// Local to Network
fun LocalWorkflow.toNetworkWorkflow() = NetworkWorkflow(
    id = id,
    title = title,
    chores = chores,
    routineInfo = routineInfo
)

fun List<LocalWorkflow>.toNetworkWorkflow() = map(LocalWorkflow::toNetworkWorkflow)

// External to Network
fun Workflow.toNetworkWorkflow() = toLocalWorkflow().toNetworkWorkflow()

@JvmName("externalWorkflowToNetworkWorkflow")
fun List<Workflow>.toNetworkWorkflow() = map(Workflow::toNetworkWorkflow)

// Network to External
fun NetworkWorkflow.toExternalWorkflow() = toLocalWorkflow().toExternalWorkflow()

@JvmName("networkWorkflowToExternalWorkflow")
fun List<NetworkWorkflow>.toExternalWorkflow() = map(NetworkWorkflow::toExternalWorkflow)