package com.example.scodd.model

import androidx.compose.runtime.Immutable

@Immutable
data class ChoreItem(
    val id: String,
    val parentChoreId: String,
    val parentWorkflowId: String,
    val isComplete: Boolean,
)