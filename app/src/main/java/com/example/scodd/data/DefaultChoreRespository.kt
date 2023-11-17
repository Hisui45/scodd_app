package com.example.scodd.data

import com.example.scodd.data.source.local.ChoreDao
import com.example.scodd.data.source.network.NetworkDataSource
import com.example.scodd.di.ApplicationScope
import com.example.scodd.di.DefaultDispatcher
import com.example.scodd.model.*
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


    override suspend fun createChore(title: String): String {
        val choreId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val chore = Chore(
            title = title,
            id = choreId,
            rooms = emptyList(),
            routineInfo = RoutineInfo(),
            isTimeModeActive = false,
            timerModeValue = 5,
            timerOption = ScoddTime.MINUTE,
            isBankModeActive = false,
            bankModeValue = 0,
            isFavorite = false
        )
        localDataSource.upsertChore(chore.toLocalChore())
        saveChoresToNetwork()
        return choreId

    }
    override suspend fun createChore(
        title: String,
        rooms: List<String>,
        workflows: List<String>,
        routineInfo: RoutineInfo,
        isTimeModeActive: Boolean,
        timerModeValue: Int,
        timerOption: ScoddTime,
        isBankModeActive: Boolean,
        bankModeValue: Int,
        isFavorite: Boolean
    ): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val choreId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }

        workflows.forEach { parentWorkflow ->
            createChoreItem(
                parentWorkflowId = parentWorkflow,
                parentChoreId = choreId
            )
        }

        val chore = Chore(
            title = title,
            id = choreId,
            rooms = rooms,
            routineInfo = routineInfo,
            isTimeModeActive = isTimeModeActive,
            timerModeValue = timerModeValue,
            timerOption = timerOption,
            isBankModeActive = isBankModeActive,
            bankModeValue = bankModeValue,
            isFavorite = isFavorite
        )
        localDataSource.upsertChore(chore.toLocalChore())
        saveChoresToNetwork()
        return choreId
    }

    override suspend fun updateChore(
        choreId: String,
        title: String,
        rooms: List<String>,
        workflows: List<String>,
        routineInfo: RoutineInfo,
        isTimeModeActive: Boolean,
        timerModeValue: Int,
        timerOption: ScoddTime,
        isBankModeActive: Boolean,
        bankModeValue: Int,
        isFavorite: Boolean
    ) {
        val existingChoreItems = getChoreItems().filter { it.parentChoreId == choreId }

        existingChoreItems
            .filter { choreItem -> workflows.none { it == choreItem.parentWorkflowId } }
            .forEach { deleteChoreItem(it.id) }

        workflows
            .filter { parentWorkflow -> existingChoreItems.none { it.parentWorkflowId == parentWorkflow } }
            .forEach { parentWorkflow ->
                createChoreItem(
                    parentWorkflowId = parentWorkflow,
                    parentChoreId = choreId
                )
            }

        val chore = getChore(choreId)?.copy(
            title = title,
            rooms = rooms,
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

    override suspend fun getChores(forceUpdate: Boolean): List<Chore> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAllChores().toExternalChore()
        }
    }


    override fun getChoresStream(): Flow<List<Chore>> {
        return localDataSource.observeAllChores().map { chores ->
            withContext(dispatcher) {
                chores.toExternalChore()
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
     * Get a Chore with the given ID. Will return null if the chore cannot be found.
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

    override suspend fun deleteAllChores() {
        localDataSource.deleteAllChores()
        saveChoresToNetwork()
    }

    override suspend fun deleteChore(choreId: String) {
        localDataSource.deleteChoreById(choreId)
        saveChoresToNetwork()
    }

    /**
     * Room
     */

    override suspend fun createRoom(title: String, selected : Boolean): String {

        // ID creation might be a complex operation, so it's executed using the supplied
        // coroutine dispatcher
        val roomId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val room = Room(
            id = roomId,
            title = title,
            selected = selected
        )
        localDataSource.upsertRoom(room.toLocalRoom())
        saveRoomsToNetwork()
        return roomId
    }

    override suspend fun updateRoom(roomId: String, title: String, selected: Boolean) {
        val room = getRoom(roomId)?.copy(
            title = title,
            selected = selected,
        ) ?: throw Exception("Room (id $roomId) not found")

        localDataSource.upsertRoom(room.toLocalRoom())
        saveRoomsToNetwork()
    }
    override suspend fun getRooms(forceUpdate: Boolean): List<Room> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAllRooms().toExternalRoom()
        }
    }
    override fun getRoomsStream(): Flow<List<Room>> {
        return localDataSource.observeAllRooms().map { rooms ->
            withContext(dispatcher) {
                rooms.toExternalRoom()
            }
        }
    }

    override suspend fun refreshRoom(roomId: String) {
        refresh()
    }

    override fun getRoomStream(roomId: String): Flow<Room?> {
        return localDataSource.observeRoomById(roomId).map { it.toExternalRoom() }
    }

    /**
     * Get a Room with the given ID. Will return null if the room cannot be found.
     *
     * @param roomId - The ID of the room
     * @param forceUpdate - true if the room should be updated from the network data source first.
     */
    override suspend fun getRoom(roomId: String, forceUpdate: Boolean): Room? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getRoomById(roomId)?.toExternalRoom()
    }

//    override suspend fun switchCheckList(roomId: String) {
//        localDataSource.toggleListType(roomId = roomId, checkList = true)
//        saveRoomsToNetwork()
//    }
//
//    override suspend fun switchChoreList(roomId: String) {
//        localDataSource.toggleListType(roomId = roomId, checkList = false)
//        saveRoomsToNetwork()
//    }

    override suspend fun deleteAllRooms() {
        localDataSource.deleteAllRooms()
        saveRoomsToNetwork()
    }

    override suspend fun deleteRoom(roomId: String) {
        localDataSource.deleteRoomById(roomId)
        saveRoomsToNetwork()
    }

    /**
     * Workflow
     */

    override suspend fun createWorkflow(title: String): String {
        // ID creation might be a complex operation, so it's executed using the supplied
        // coroutine dispatcher
        val workflowId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val workflow = Workflow(
            id = workflowId,
            title = title,
            isCheckList = false,
            routineInfo = RoutineInfo()
        )
        localDataSource.upsertWorkflow(workflow.toLocalWorkflow())
        saveWorkflowsToNetwork()
        return workflowId
    }


    override suspend fun createWorkflow(
            title: String,
            chores : List<String>): String {
        // ID creation might be a complex operation, so it's executed using the supplied
        // coroutine dispatcher
        val workflowId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }

        chores.forEach { parentChore ->
            createChoreItem(
                parentWorkflowId = workflowId,
                parentChoreId = parentChore
            )
        }

        val workflow = Workflow(
            id = workflowId,
            title = title,
            isCheckList = false,
            routineInfo = RoutineInfo()
        )
        localDataSource.upsertWorkflow(workflow.toLocalWorkflow())
        saveWorkflowsToNetwork()
        return workflowId
    }

    override suspend fun updateWorkflow(
        workflowId: String,
        title: String,
        isCheckList: Boolean,
        routineInfo: RoutineInfo
    ) {
        val workflow = getWorkflow(workflowId)?.copy(
            title = title,
            isCheckList = isCheckList,
            routineInfo = routineInfo
        ) ?: throw Exception("Workflow (id $workflowId) not found")

        localDataSource.upsertWorkflow(workflow.toLocalWorkflow())
        saveWorkflowsToNetwork()
    }

    override suspend fun updateWorkflow(
        workflowId: String,
        selectedItems: List<String>
    ){

        val existingChoreItems = getChoreItems().filter { it.parentWorkflowId == workflowId }

        val parentChoreIdsToAdd = selectedItems.subtract(existingChoreItems.map { it.parentChoreId }.toSet())
        parentChoreIdsToAdd.forEach { parentChoreId ->
            createChoreItem(parentChoreId = parentChoreId, parentWorkflowId = workflowId)
        }

    }

    override suspend fun updateTitle(workflowId: String, title: String) {
        val workflow = getWorkflow(workflowId)?.copy(
            title = title,
        ) ?: throw Exception("Workflow (id $workflowId) not found")

        localDataSource.upsertWorkflow(workflow.toLocalWorkflow())
        saveWorkflowsToNetwork()
    }

    override suspend fun getWorkflows(forceUpdate: Boolean): List<Workflow> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAllWorkflows().toExternalWorkflow()
        }
    }
    override fun getWorkflowsStream(): Flow<List<Workflow>> {
        return localDataSource.observeAllWorkflows().map { workflows ->
            withContext(dispatcher) {
                workflows.toExternalWorkflow()
            }
        }
    }

    override suspend fun refreshWorkflow(workflowId: String) {
        refresh()
    }

    override fun getWorkflowStream(workflowId: String): Flow<Workflow?> {
        return localDataSource.observeWorkflowById(workflowId).map { it.toExternalWorkflow() }
    }

    /**
     * Get a Workflow with the given ID. Will return null if the workflow cannot be found.
     *
     * @param workflowId - The ID of the workflow
     * @param forceUpdate - true if the workflow should be updated from the network data source first.
     */
    override suspend fun getWorkflow(workflowId: String, forceUpdate: Boolean): Workflow? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getWorkflowById(workflowId)?.toExternalWorkflow()
    }

    override suspend fun switchCheckList(workflowId: String) {
        localDataSource.toggleListType(workflowId = workflowId, checkList = true)
        saveWorkflowsToNetwork()
    }

    override suspend fun switchChoreList(workflowId: String) {
        localDataSource.toggleListType(workflowId = workflowId, checkList = false)
        saveWorkflowsToNetwork()
    }

    override suspend fun deleteAllWorkflows() {
        localDataSource.deleteAllWorkflows()
        saveWorkflowsToNetwork()
    }

    override suspend fun deleteWorkflow(workflowId: String) {
        localDataSource.deleteWorkflowById(workflowId)
        saveWorkflowsToNetwork()
    }

    /**
     * Chore Item
     */

    override suspend fun createChoreItem(
        choreItemId: String,
        parentChoreId: String,
        parentWorkflowId: String,
    ): String {
        val choreItem = ChoreItem(
            id = choreItemId,
            parentChoreId = parentChoreId,
            parentWorkflowId = parentWorkflowId,
            isComplete = false,
        )
        localDataSource.upsertChoreItem(choreItem.toLocalChoreItem())
        saveChoreItemsToNetwork()
        return choreItemId
    }

    override suspend fun createChoreItem(
        parentChoreId: String,
        parentWorkflowId: String,
    ): String {
        // ID creation might be a complex operation, so it's executed using the supplied
        // coroutine dispatcher
        val choreItemId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val choreItem = ChoreItem(
            id = choreItemId,
            parentChoreId = parentChoreId,
            parentWorkflowId = parentWorkflowId,
            isComplete = false,

        )
        localDataSource.upsertChoreItem(choreItem.toLocalChoreItem())
        saveChoreItemsToNetwork()
        return choreItemId
    }

    override suspend fun updateChoreItem(
        choreItemId: String,
        parentChoreId: String,
        parentWorkflowId: String,
        isComplete: Boolean
    ) {
        val choreItem = getChoreItem(choreItemId)?.copy(
            id = choreItemId,
            parentChoreId = parentChoreId,
            parentWorkflowId = parentWorkflowId,
            isComplete = isComplete,
        ) ?: throw Exception("ChoreItem (id $choreItemId) not found")

        localDataSource.upsertChoreItem(choreItem.toLocalChoreItem())
        saveChoreItemsToNetwork()
    }
    override suspend fun getChoreItems(forceUpdate: Boolean): List<ChoreItem> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAllChoreItems().toExternalChoreItem()
        }
    }
    override fun getChoreItemsStream(): Flow<List<ChoreItem>> {
        return localDataSource.observeAllChoreItems().map { choreItems ->
            withContext(dispatcher) {
                choreItems.toExternalChoreItem()
            }
        }
    }

    override suspend fun refreshChoreItem(choreItemId: String) {
        refresh()
    }

    override fun getChoreItemStream(choreItemId: String): Flow<ChoreItem?> {
        return localDataSource.observeChoreItemById(choreItemId).map { it.toExternalChoreItem() }
    }

    /**
     * Get a ChoreItem with the given ID. Will return null if the workflow cannot be found.
     *
     * @param choreItemId - The ID of the workflow
     * @param forceUpdate - true if the workflow should be updated from the network data source first.
     */
    override suspend fun getChoreItem(choreItemId: String, forceUpdate: Boolean): ChoreItem? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getChoreItemById(choreItemId)?.toExternalChoreItem()
    }

    override suspend fun completeChoreItem(choreItemId: String) {
        localDataSource.updateChoreItem(choreItemId = choreItemId, complete = true)
        saveChoreItemsToNetwork()
    }

    override suspend fun activateChoreItem(choreItemId: String) {
        localDataSource.updateChoreItem(choreItemId = choreItemId, complete = false)
        saveChoreItemsToNetwork()
    }

    override suspend fun deleteAllChoreItems() {
        localDataSource.deleteAllChoreItems()
        saveChoreItemsToNetwork()
    }

    override suspend fun deleteChoreItem(choreItemId: String) {
        localDataSource.deleteChoreItemById(choreItemId)
        saveChoreItemsToNetwork()
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

            val remoteRooms = networkDataSource.loadRooms()
            localDataSource.deleteAllRooms()
            localDataSource.upsertAllRooms(remoteRooms.toLocalRoom())

            val remoteWorkflows = networkDataSource.loadWorkflows()
            localDataSource.deleteAllWorkflows()
            localDataSource.upsertAllWorkflows(remoteWorkflows.toLocalWorkflow())

            val remoteChoreItems = networkDataSource.loadChoreItems()
            localDataSource.deleteAllChoreItems()
            localDataSource.upsertAllChoreItems(remoteChoreItems.toLocalChoreItem())
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

    /**
     * Send the workflows from the local data source to the network data source
     *
     * Returns immediately after launching the job. Real apps may want to suspend here until the
     * operation is complete or (better) use WorkManager to schedule this work. Both approaches
     * should provide a mechanism for failures to be communicated back to the user so that
     * they are aware that their data isn't being backed up.
     */
    private fun saveWorkflowsToNetwork() {
        scope.launch {
            try {
                val localWorkflows = localDataSource.getAllWorkflows()
                val networkWorkflows = withContext(dispatcher) {
                    localWorkflows.toNetworkWorkflow()
                }
                networkDataSource.saveWorkflows(networkWorkflows)
            } catch (e: Exception) {
                // In a real app you'd handle the exception e.g. by exposing a `networkStatus` flow
                // to an app level UI state holder which could then display a Toast message.
            }
        }
    }

    /**
     * Send the Rooms from the local data source to the network data source
     *
     * Returns immediately after launching the job. Real apps may want to suspend here until the
     * operation is complete or (better) use WorkManager to schedule this work. Both approaches
     * should provide a mechanism for failures to be communicated back to the user so that
     * they are aware that their data isn't being backed up.
     */
    private fun saveRoomsToNetwork() {
        scope.launch {
            try {
                val localRooms = localDataSource.getAllRooms()
                val networkRooms = withContext(dispatcher) {
                    localRooms.toNetworkRoom()
                }
                networkDataSource.saveRooms(networkRooms)
            } catch (e: Exception) {
                // In a real app you'd handle the exception e.g. by exposing a `networkStatus` flow
                // to an app level UI state holder which could then display a Toast message.
            }
        }
    }


    /**
     * Send the choreItem from the local data source to the network data source
     *
     * Returns immediately after launching the job. Real apps may want to suspend here until the
     * operation is complete or (better) use WorkManager to schedule this work. Both approaches
     * should provide a mechanism for failures to be communicated back to the user so that
     * they are aware that their data isn't being backed up.
     */
    private fun saveChoreItemsToNetwork() {
        scope.launch {
            try {
                val localChoreItems = localDataSource.getAllChoreItems()
                val networkChoreItems = withContext(dispatcher) {
                    localChoreItems.toNetworkChoreItem()
                }
                networkDataSource.saveChoreItems(networkChoreItems)
            } catch (e: Exception) {
                // In a real app you'd handle the exception e.g. by exposing a `networkStatus` flow
                // to an app level UI state holder which could then display a Toast message.
            }
        }
    }
}