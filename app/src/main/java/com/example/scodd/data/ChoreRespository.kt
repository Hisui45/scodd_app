package com.example.scodd.data

import com.example.scodd.model.*
import kotlinx.coroutines.flow.Flow

interface ChoreRepository {

    /**
     * Chore
     */
    fun getChoresStream(): Flow<List<Chore>>

    suspend fun getChores(forceUpdate: Boolean = false): List<Chore>

    suspend fun refresh()

    fun getChoreStream(choreId: String): Flow<Chore?>

    suspend fun getChore(choreId: String, forceUpdate: Boolean = false): Chore?

    suspend fun refreshChore(choreId: String)

    suspend fun createChore(title: String, rooms: List<String>, workflows: List<String>, routineInfo: RoutineInfo,
                            isTimeModeActive: Boolean, timerModeValue: Int, timerOption: ScoddTime,
                            isBankModeActive: Boolean, bankModeValue: Int,
                            isFavorite: Boolean): String

    suspend fun createChore(title: String): String

    suspend fun updateChore(choreId: String, title: String, rooms: List<String>, workflows: List<String>, routineInfo: RoutineInfo,
                            isTimeModeActive: Boolean, timerModeValue: Int, timerOption: ScoddTime,
                            isBankModeActive: Boolean, bankModeValue: Int,
                            isFavorite: Boolean)

    suspend fun favoriteChore(choreId: String)

    suspend fun unFavoriteChore(choreId: String)

    suspend fun deleteAllChores()

    suspend fun deleteChore(choreId: String)

    /**
     * Room
     */
    fun getRoomsStream(): Flow<List<Room>>

    suspend fun getRooms(forceUpdate: Boolean = false): List<Room>

    fun getRoomStream(roomId: String): Flow<Room?>

    suspend fun getRoom(roomId: String, forceUpdate: Boolean = false): Room?

    suspend fun refreshRoom(roomId: String)

    suspend fun createRoom(title: String, selected : Boolean): String

    suspend fun updateRoom(roomId: String, title: String, selected : Boolean)

//    suspend fun switchCheckList(roomId: String)
//
//    suspend fun switchChoreList(roomId: String)

    suspend fun deleteAllRooms()

    suspend fun deleteRoom(roomId: String)


    /**
     * Workflow
     */
    fun getWorkflowsStream(): Flow<List<Workflow>>

    suspend fun getWorkflows(forceUpdate: Boolean = false): List<Workflow>

    fun getWorkflowStream(workflowId: String): Flow<Workflow?>

    suspend fun getWorkflow(workflowId: String, forceUpdate: Boolean = false): Workflow?

    suspend fun refreshWorkflow(workflowId: String)

    suspend fun createWorkflow(title: String): String

    suspend fun createWorkflow(title: String, chores: List<String>): String

    suspend fun updateWorkflow(workflowId: String, title: String, isCheckList: Boolean, routineInfo: RoutineInfo)

    suspend fun updateWorkflow(workflowId: String, selectedItems: List<String>)

    suspend fun updateTitle(workflowId: String, title:String)
    suspend fun switchCheckList(workflowId: String)

    suspend fun switchChoreList(workflowId: String)

    suspend fun deleteAllWorkflows()

    suspend fun deleteWorkflow(workflowId: String)

    /**
     * Chore Item
     */

    suspend fun createChoreItem(parentChoreId : String, parentWorkflowId : String): String

    suspend fun createChoreItem(choreItemId: String,parentChoreId : String, parentWorkflowId : String): String

    suspend fun updateChoreItem(choreItemId : String, parentChoreId : String, parentWorkflowId : String, isComplete : Boolean)

    suspend fun getChoreItem(choreItemId: String, forceUpdate: Boolean = false): ChoreItem?

    suspend fun getChoreItems(forceUpdate: Boolean = false): List<ChoreItem>

    fun getChoreItemStream(choreItemId: String): Flow<ChoreItem?>

    fun getChoreItemsStream(): Flow<List<ChoreItem>>

    suspend fun refreshChoreItem(choreItemId: String)

    suspend fun completeChoreItem(choreItemId: String)

    suspend fun activateChoreItem(choreItemId: String)

    suspend fun deleteAllChoreItems()

    suspend fun deleteChoreItem(choreItemId: String)


}