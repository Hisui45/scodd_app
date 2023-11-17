package com.example.scodd.data.source.network

import com.example.scodd.model.ChoreItem
import com.example.scodd.model.RoutineInfo

data class NetworkWorkflow(
    var id: String,
    var title : String,
    var isCheckList: Boolean,
    var routineInfo: RoutineInfo
)