package com.example.scodd.data.source.network

import com.example.scodd.model.Room
data class NetworkChoreItem(
    val id: String,
    val parentChoreId: String,
    var parentWorkflowId: String,
    var isComplete: Boolean,
)