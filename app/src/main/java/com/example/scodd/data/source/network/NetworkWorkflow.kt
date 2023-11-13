package com.example.scodd.data.source.network

import com.example.scodd.model.ChoreItem
import com.example.scodd.model.RoutineInfo

data class NetworkWorkflow(
    val id: String,
    val title : String,
    val chores : List<ChoreItem> = emptyList(),
    var routineInfo: RoutineInfo
)