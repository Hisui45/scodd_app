package com.example.scodd.model

import androidx.compose.runtime.Immutable

@Immutable
data class Chore(
    val id: String,
    val title: String,
    val rooms: List<Room> = emptyList(),
    val workflows: List<String> = emptyList(), //Might change to ids
    val routineInfo: RoutineInfo = RoutineInfo(),
    val isTimeModeActive: Boolean = false,
    val timerModeValue: Int = 0,
    val timerOption: ScoddTime = ScoddTime.MINUTE,
    val isBankModeActive: Boolean = false,
    val bankModeValue: Int = 0,
    val isFavorite: Boolean = false
)

@Immutable
data class Room(
    val id: String,
    val title : String,
    val selected: Boolean = false
)