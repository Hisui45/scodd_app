package com.example.scodd.data.source.network

import com.example.scodd.model.Room
import com.example.scodd.model.RoutineInfo
import com.example.scodd.model.ScoddTime

data class NetworkChore(
    val id : String,
    var title: String,
    var rooms : List<String>,
    var routineInfo: RoutineInfo,
    val isTimeModeActive: Boolean,
    var timerModeValue: Int,
    var timerOption: ScoddTime,
    var isBankModeActive: Boolean,
    var bankModeValue: Int,
    var isFavorite: Boolean
)
