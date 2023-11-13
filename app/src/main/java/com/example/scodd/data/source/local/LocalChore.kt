package com.example.scodd.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.scodd.model.Room
import com.example.scodd.model.RoutineInfo
import com.example.scodd.model.ScoddTime

@Entity(
    tableName = "chore"
)
data class LocalChore(
    @PrimaryKey val id: String,
    var title: String,
    var rooms : List<Room>,
    var routineInfo: RoutineInfo,
    val isTimeModeActive: Boolean,
    var timerModeValue: Int,
    var timerOption: ScoddTime,
    var isBankModeActive: Boolean,
    var bankModeValue: Int,
    var isFavorite: Boolean
)