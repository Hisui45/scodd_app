package com.example.scodd.data

import com.example.scodd.model.ChoreItem
import com.example.scodd.model.RoutineInfo
import com.example.scodd.model.Workflow
import kotlinx.coroutines.flow.Flow

interface WorkflowRepository {

    fun getWorkflowsStream(): Flow<List<Workflow>>

    suspend fun getWorkflows(forceUpdate: Boolean = false): List<Workflow>

    suspend fun refresh()

    fun getWorkflowStream(workflowId: String): Flow<Workflow?>

    suspend fun getWorkflow(workflowId: String, forceUpdate: Boolean = false): Workflow?

    suspend fun refreshWorkflow(workflowId: String)

    suspend fun createWorkflow(title: String, chores: List<ChoreItem>, routineInfo: RoutineInfo): String

    suspend fun updateWorkflow(workflowId: String,title: String, chores: List<ChoreItem>, routineInfo: RoutineInfo)

//    suspend fun completeWorkflow(workflowId: String)

//    suspend fun clearCompletedWorkflows()

    suspend fun deleteAllWorkflows()

    suspend fun deleteWorkflow(workflowId: String)
}