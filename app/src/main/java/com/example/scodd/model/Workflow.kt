package com.example.scodd.model

import androidx.compose.runtime.Immutable

@Immutable
data class Workflow(
    val id: String,
    val title : String,
    val isCheckList: Boolean = false,
    val routineInfo: RoutineInfo = RoutineInfo()
)

val ROUNDUP: String = "roundup"




