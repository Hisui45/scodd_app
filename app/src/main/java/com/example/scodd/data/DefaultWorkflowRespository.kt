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
class DefaultWorkflowRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: ChoreDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : WorkflowRepository {

    override suspend fun createWorkflow(title: String, chores: List<ChoreItem>, routineInfo: RoutineInfo): String {
        
        // ID creation might be a complex operation, so it's executed using the supplied
        // coroutine dispatcher
        val workflowId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val workflow = Workflow(
            id = workflowId,
            title = title,
            chores = chores,
            routineInfo = routineInfo
        )
        localDataSource.upsertWorkflow(workflow.toLocalWorkflow())
        saveWorkflowsToNetwork()
        return workflowId
    }

    override suspend fun updateWorkflow(
        workflowId: String,
        title: String,
        chores: List<ChoreItem>,
        routineInfo: RoutineInfo
    ) {
        val workflow = getWorkflow(workflowId)?.copy(
            title = title,
            chores = chores,
            routineInfo = routineInfo
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
     * Get a Task with the given ID. Will return null if the workflow cannot be found.
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

//    override suspend fun favoriteWorkflow(workflowId: String) {
//        localDataSource.updateFavorite(workflowId = workflowId, favorite = true)
//        saveWorkflowsToNetwork()
//    }
//
//    override suspend fun unFavoriteWorkflow(workflowId: String) {
//        localDataSource.updateFavorite(workflowId = workflowId, favorite = false)
//        saveWorkflowsToNetwork()
//    }

//    override suspend fun activateTask(workflowId: String) {
//        localDataSource.updateCompleted(workflowId = workflowId, completed = false)
//        saveWorkflowsToNetwork()
//    }

//    override suspend fun clearCompletedWorkflows() {
//        localDataSource.deleteCompleted()
//        saveWorkflowsToNetwork()
//    }

    override suspend fun deleteAllWorkflows() {
        localDataSource.deleteAllWorkflows()
        saveWorkflowsToNetwork()
    }

    override suspend fun deleteWorkflow(workflowId: String) {
        localDataSource.deleteWorkflowById(workflowId)
        saveWorkflowsToNetwork()
    }

    /**
     * The following methods load workflows from (refresh), and save workflows to, the network.
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
            val remoteWorkflows = networkDataSource.loadWorkflows()
            localDataSource.deleteAllWorkflows()
            localDataSource.upsertAllWorkflows(remoteWorkflows.toLocalWorkflow())
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
}