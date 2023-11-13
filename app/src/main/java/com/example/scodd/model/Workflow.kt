package com.example.scodd.model

import androidx.compose.runtime.Immutable

@Immutable
data class Workflow(
    val id: String,
    val title : String,
    val chores : List<ChoreItem> = emptyList(),
    var routineInfo: RoutineInfo = RoutineInfo()
)
@Immutable
data class ChoreItem(
    val id: String,
    val parentChoreId: String,
    val title: String,
    val isComplete: Boolean
)



