package com.example.scodd.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the chore table.
 */
@Dao
interface ChoreDao {

    /**
     * Observes list of chores.
     *
     * @return all chores.
     */
    @Query("SELECT * FROM chore")
    fun observeAllChores(): Flow<List<LocalChore>>

    /**
     * Observes a single chore.
     *
     * @param choreId the chore id.
     * @return the chore with choreId.
     */
    @Query("SELECT * FROM chore WHERE id = :choreId")
    fun observeChoreById(choreId: String): Flow<LocalChore>

    /**
     * Select all chores from the chores table.
     *
     * @return all chores.
     */
    @Query("SELECT * FROM chore")
    suspend fun getAllChores(): List<LocalChore>

    /**
     * Select a chore by id.
     *
     * @param choreId the chore id.
     * @return the chore with choreId.
     */
    @Query("SELECT * FROM chore WHERE id = :choreId")
    suspend fun getChoreById(choreId: String): LocalChore?

    /**
     * Insert or update a chore in the database. If a chore already exists, replace it.
     *
     * @param chore the chore to be inserted or updated.
     */
    @Upsert
    suspend fun upsertChore(chore: LocalChore)

    /**
     * Insert or update chores in the database. If a chore already exists, replace it.
     *
     * @param chores the chores to be inserted or updated.
     */

    @Insert
    fun insertAllChores(chores: List<LocalChore>)

    @Upsert
    suspend fun upsertAllChores(chores: List<LocalChore>)

    /**
     * Update the favorite status of a chore
     *
     * @param choreId id of the chore
     * @param favorite status to be updated
     */

    @Query("UPDATE chore SET isFavorite = :favorite WHERE id = :choreId")
    suspend fun updateFavorite(choreId: String, favorite: Boolean)

    /**
     * Delete a chore by id.
     *
     * @return the number of chores deleted. This should always be 1.
     */
    @Query("DELETE FROM chore WHERE id = :choreId")
    suspend fun deleteChoreById(choreId: String): Int

    /**
     * Delete all chores.
     */
    @Query("DELETE FROM chore")
    suspend fun deleteAllChores()

//    /**
//     * Delete all completed chores from the table.
//     *
//     * @return the number of chores deleted.
//     */
//    @Query("DELETE FROM chore WHERE isCompleted = 1")
//    suspend fun deleteCompleted(): Int

    /**
     * Observes list of rooms.
     *
     * @return all rooms.
     */
    @Query("SELECT * FROM room")
    fun observeAllRooms(): Flow<List<LocalRoom>>

    /**
     * Observes a single room.
     *
     * @param roomId the room id.
     * @return the room with choreId.
     */
    @Query("SELECT * FROM room WHERE id = :roomId")
    fun observeRoomById(roomId: String): Flow<LocalRoom>

    /**
     * Select all rooms from the rooms table.
     *
     * @return all rooms.
     */
    @Query("SELECT * FROM room")
    suspend fun getAllRooms(): List<LocalRoom>

    /**
     * Select a room by id.
     *
     * @param roomId the room id.
     * @return the room with roomId.
     */
    @Query("SELECT * FROM room WHERE id = :roomId")
    suspend fun getRoomById(roomId: String): LocalRoom?

    /**
     * Insert or update a room in the database. If a room already exists, replace it.
     *
     * @param room the room to be inserted or updated.
     */
    @Upsert
    suspend fun upsertRoom(room: LocalRoom)

    /**
     * Insert or update rooms in the database. If a room already exists, replace it.
     *
     * @param rooms the rooms to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAllRooms(rooms: List<LocalRoom>)

    @Insert
    fun insertAllRooms(rooms: List<LocalRoom>)
    /**
     * Delete a room by id.
     *
     * @return the number of rooms deleted. This should always be 1.
     */
    @Query("DELETE FROM room WHERE id = :roomId")
    suspend fun deleteRoomById(roomId: String): Int

    /**
     * Delete all rooms.
     */
    @Query("DELETE FROM room")
    suspend fun deleteAllRooms()

    //Workflow
    /**
     * Observes list of workflows.
     *
     * @return all workflows.
     */
    @Query("SELECT * FROM workflow")
    fun observeAllWorkflows(): Flow<List<LocalWorkflow>>

    /**
     * Observes a single workflow.
     *
     * @param workflowId the workflow id.
     * @return the workflow with choreId.
     */
    @Query("SELECT * FROM workflow WHERE id = :workflowId")
    fun observeWorkflowById(workflowId: String): Flow<LocalWorkflow>

    /**
     * Select all workflows from the workflows table.
     *
     * @return all workflows.
     */
    @Query("SELECT * FROM workflow")
    suspend fun getAllWorkflows(): List<LocalWorkflow>

    /**
     * Select a workflow by id.
     *
     * @param workflowId the workflow id.
     * @return the workflow with workflowId.
     */
    @Query("SELECT * FROM workflow WHERE id = :workflowId")
    suspend fun getWorkflowById(workflowId: String): LocalWorkflow?

    /**
     * Insert or update a workflow in the database. If a workflow already exists, replace it.
     *
     * @param workflow the workflow to be inserted or updated.
     */
    @Upsert
    suspend fun upsertWorkflow(workflow: LocalWorkflow)

    /**
     * Insert or update workflows in the database. If a workflow already exists, replace it.
     *
     * @param workflows the workflows to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAllWorkflows(workflows: List<LocalWorkflow>)

    @Insert
    fun insertAllWorkflows(workflows: List<LocalWorkflow>)
    /**
     * Delete a workflow by id.
     *
     * @return the number of workflows deleted. This should always be 1.
     */
    @Query("DELETE FROM workflow WHERE id = :workflowId")
    suspend fun deleteWorkflowById(workflowId: String): Int

    /**
     * Delete all workflows.
     */
    @Query("DELETE FROM workflow")
    suspend fun deleteAllWorkflows()

    /**
     * Update the list type of workflow
     *
     * @param workflowId id of the task
     * @param checkList status to be updated
     */
    @Query("UPDATE workflow SET isCheckList = :checkList WHERE id = :workflowId")
    suspend fun toggleListType(workflowId: String, checkList: Boolean)

    //Chore Item
    /**
     * Observes list of choreItems.
     *
     * @return all choreItems.
     */
    @Query("SELECT * FROM choreItem")
    fun observeAllChoreItems(): Flow<List<LocalChoreItem>>

    /**
     * Observes a single choreItem.
     *
     * @param choreItemId the choreItem id.
     * @return the choreItem with choreId.
     */
    @Query("SELECT * FROM choreItem WHERE id = :choreItemId")
    fun observeChoreItemById(choreItemId: String): Flow<LocalChoreItem>

    /**
     * Select all choreItems from the choreItems table.
     *
     * @return all choreItems.
     */
    @Query("SELECT * FROM choreItem")
    suspend fun getAllChoreItems(): List<LocalChoreItem>

    /**
     * Select a choreItem by id.
     *
     * @param choreItemId the choreItem id.
     * @return the choreItem with choreItemId.
     */
    @Query("SELECT * FROM choreItem WHERE id = :choreItemId")
    suspend fun getChoreItemById(choreItemId: String): LocalChoreItem?

    /**
     * Insert or update a choreItem in the database. If a choreItem already exists, replace it.
     *
     * @param choreItem the choreItem to be inserted or updated.
     */
    @Upsert
    suspend fun upsertChoreItem(choreItem: LocalChoreItem)

    /**
     * Insert or update choreItems in the database. If a choreItem already exists, replace it.
     *
     * @param choreItems the choreItems to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAllChoreItems(choreItems: List<LocalChoreItem>)

    @Insert
    fun insertAllChoreItems(choreItems: List<LocalChoreItem>)
    /**
     * Delete a choreItem by id.
     *
     * @return the number of choreItems deleted. This should always be 1.
     */
    @Query("DELETE FROM choreItem WHERE id = :choreItemId")
    suspend fun deleteChoreItemById(choreItemId: String): Int

    /**
     * Delete all choreItems.
     */
    @Query("DELETE FROM choreItem")
    suspend fun deleteAllChoreItems()

    /**
     * Update the list type of choreItem
     *
     * @param choreItemId id of the task
     * @param complete status to be updated
     */
    @Query("UPDATE choreItem SET isComplete = :complete WHERE id = :choreItemId")
    suspend fun updateChoreItem(choreItemId: String, complete: Boolean)

    /**
     * Observes list of modes.
     *
     * @return all modes.
     */
    @Query("SELECT * FROM mode")
    fun observeAllModes(): Flow<List<LocalMode>>

    /**
     * Observes a single mode.
     *
     * @param id the mode id.
     * @return the mode with choreId.
     */
    @Query("SELECT * FROM mode WHERE id = :id")
    fun observeModeById(id: String): Flow<LocalMode>

    /**
     * Select all modes from the modes table.
     *
     * @return all modes.
     */
    @Query("SELECT * FROM mode")
    suspend fun getAllModes(): List<LocalMode>

    /**
     * Select a mode by id.
     *
     * @param id the mode id.
     * @return the mode with mode.
     */
    @Query("SELECT * FROM mode WHERE id = :id")
    suspend fun getModeById(id: String): LocalMode?

    /**
     * Insert or update a mode in the database. If a mode already exists, replace it.
     *
     * @param mode the mode to be inserted or updated.
     */
    @Upsert
    suspend fun upsertMode(mode: LocalMode)

    /**
     * Insert or update modes in the database. If a mode already exists, replace it.
     *
     * @param modes the modes to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAllModes(modes: List<LocalMode>)

    @Insert
    fun insertAllModes(modes: List<LocalMode>)
    /**
     * Delete a mode by id.
     *
     * @return the number of modes deleted. This should always be 1.
     */
    @Query("DELETE FROM mode WHERE id = :id")
    suspend fun deleteModeById(id: String): Int

    /**
     * Delete all modes.
     */
    @Query("DELETE FROM mode")
    suspend fun deleteAllModes()


    /**
     * User
     */

    /**
     * Observes list of users.
     *
     * @return all users.
     */
    @Query("SELECT * FROM user")
    fun observeAllUsers(): Flow<List<LocalUser>>

    /**
     * Observes a single user.
     *
     * @param userId the user id.
     * @return the user with choreId.
     */
    @Query("SELECT * FROM user WHERE id = :userId")
    fun observeUserById(userId: String): Flow<LocalUser>

    /**
     * Select all users from the users table.
     *
     * @return all users.
     */
    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<LocalUser>

    /**
     * Select a user by id.
     *
     * @param userId the user id.
     * @return the user with userId.
     */
    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId: String): LocalUser?

    /**
     * Insert or update a user in the database. If a user already exists, replace it.
     *
     * @param user the user to be inserted or updated.
     */
    @Upsert
    suspend fun upsertUser(user: LocalUser)

    /**
     * Insert or update users in the database. If a user already exists, replace it.
     *
     * @param users the users to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAllUsers(users: List<LocalUser>)

    @Insert
    fun insertAllUsers(users: List<LocalUser>)
    /**
     * Delete a user by id.
     *
     * @return the number of users deleted. This should always be 1.
     */
    @Query("DELETE FROM user WHERE id = :userId")
    suspend fun deleteUserById(userId: String): Int

    /**
     * Delete all users.
     */
    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()


}