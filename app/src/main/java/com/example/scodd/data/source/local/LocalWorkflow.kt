package com.example.scodd.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.scodd.model.ChoreItem
import com.example.scodd.model.RoutineInfo


@Entity(
    tableName = "workflow"
)
data class LocalWorkflow(
    @PrimaryKey val id: String,
    var title : String,
    var isCheckList: Boolean,
    var routineInfo: RoutineInfo
)