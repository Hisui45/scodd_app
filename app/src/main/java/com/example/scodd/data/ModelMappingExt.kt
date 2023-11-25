package com.example.scodd.data

import com.example.scodd.data.source.local.*
import com.example.scodd.data.source.network.*
import com.example.scodd.model.*

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
    isCheckList = isCheckList,
    routineInfo = routineInfo
)

fun List<Workflow>.toLocalWorkflow() = map(Workflow::toLocalWorkflow)

//Local to External
fun LocalWorkflow.toExternalWorkflow() = Workflow(
    id = id,
    title = title,
    isCheckList = isCheckList,
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
    isCheckList = isCheckList,
    routineInfo = routineInfo
)

@JvmName("networkWorkflowToLocalWorkflow")
fun List<NetworkWorkflow>.toLocalWorkflow() = map(NetworkWorkflow::toLocalWorkflow)

// Local to Network
fun LocalWorkflow.toNetworkWorkflow() = NetworkWorkflow(
    id = id,
    title = title,
    isCheckList = isCheckList,
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

/**
 * ChoreItem
 */

// External to local
fun ChoreItem.toLocalChoreItem() = LocalChoreItem(
    id = id,
    parentChoreId = parentChoreId,
    parentWorkflowId = parentWorkflowId,
    isComplete = isComplete,
)

fun List<ChoreItem>.toLocalChoreItem() = map(ChoreItem::toLocalChoreItem)

//Local to External
fun LocalChoreItem.toExternalChoreItem() = ChoreItem(
    id = id,
    parentChoreId = parentChoreId,
    parentWorkflowId = parentWorkflowId,
    isComplete = isComplete,
)

// Note: JvmName is used to provide a unique name for each extension function with the same name.
// Without this, type erasure will cause compiler errors because these methods will have the same
// signature on the JVM.
@JvmName("localChoreItemToExternalChoreItem")
fun List<LocalChoreItem>.toExternalChoreItem() = map(LocalChoreItem::toExternalChoreItem)

// Network to Local
fun NetworkChoreItem.toLocalChoreItem() = LocalChoreItem(
    id = id,
    parentChoreId = parentChoreId,
    parentWorkflowId = parentWorkflowId,
    isComplete = isComplete,
)

@JvmName("networkChoreItemToLocalChoreItem")
fun List<NetworkChoreItem>.toLocalChoreItem() = map(NetworkChoreItem::toLocalChoreItem)

// Local to Network
fun LocalChoreItem.toNetworkChoreItem() = NetworkChoreItem(
    id = id,
    parentChoreId = parentChoreId,
    parentWorkflowId = parentWorkflowId,
    isComplete = isComplete,
)

fun List<LocalChoreItem>.toNetworkChoreItem() = map(LocalChoreItem::toNetworkChoreItem)

// External to Network
fun ChoreItem.toNetworkChoreItem() = toLocalChoreItem().toNetworkChoreItem()

@JvmName("externalChoreItemToNetworkChoreItem")
fun List<ChoreItem>.toNetworkChoreItem() = map(ChoreItem::toNetworkChoreItem)

// Network to External
fun NetworkChoreItem.toExternalChoreItem() = toLocalChoreItem().toExternalChoreItem()

@JvmName("networkChoreItemToExternalChoreItem")
fun List<NetworkChoreItem>.toExternalChoreItem() = map(NetworkChoreItem::toExternalChoreItem)

/**
 * Mode
 */
// External to local
fun Mode.toLocalMode() = LocalMode(
    id = id,
    selectedWorkflows = selectedWorkflows,
    workflowChores = workflowChores,
    chores = chores,
    rooms = rooms
)

fun List<Mode>.toLocalMode() = map(Mode::toLocalMode)

//Local to External
fun LocalMode.toExternalMode() = Mode(
    id = id,
    selectedWorkflows = selectedWorkflows,
    workflowChores = workflowChores,
    chores = chores,
    rooms = rooms
)

// Note: JvmName is used to provide a unique name for each extension function with the same name.
// Without this, type erasure will cause compiler errors because these methods will have the same
// signature on the JVM.
@JvmName("localModeToExternalMode")
fun List<LocalMode>.toExternalMode() = map(LocalMode::toExternalMode)

// Network to Local
fun NetworkMode.toLocalMode() = LocalMode(
    id = id,
    selectedWorkflows = selectedWorkflows,
    workflowChores = workflowChores,
    chores = chores,
    rooms = rooms
)

@JvmName("networkModeToLocalMode")
fun List<NetworkMode>.toLocalMode() = map(NetworkMode::toLocalMode)

// Local to Network
fun LocalMode.toNetworkMode() = NetworkMode(
    id = id,
    selectedWorkflows = selectedWorkflows,
    workflowChores = workflowChores,
    chores = chores,
    rooms = rooms
)

fun List<LocalMode>.toNetworkMode() = map(LocalMode::toNetworkMode)

// External to Network
fun Mode.toNetworkMode() = toLocalMode().toNetworkMode()

@JvmName("externalModeToNetworkMode")
fun List<Mode>.toNetworkMode() = map(Mode::toNetworkMode)

// Network to External
fun NetworkMode.toExternalMode() = toLocalMode().toExternalMode()

@JvmName("networkModeToExternalMode")
fun List<NetworkMode>.toExternalMode() = map(NetworkMode::toExternalMode)