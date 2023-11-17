package com.example.scodd.model

import androidx.compose.runtime.Immutable

@Immutable
data class Chore(
    val id: String,
    val title: String,
    val rooms: List<String> = emptyList(),
    val routineInfo: RoutineInfo = RoutineInfo(),
    val isTimeModeActive: Boolean = false,
    val timerModeValue: Int = 0,
    val timerOption: ScoddTime = ScoddTime.MINUTE,
    val isBankModeActive: Boolean = false,
    val bankModeValue: Int = 0,
    val isFavorite: Boolean = false
)

