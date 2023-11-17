package com.example.scodd.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.scodd.model.Room

@Entity(
    tableName = "choreItem"
)
data class LocalChoreItem(
    @PrimaryKey val id: String,
    var parentChoreId: String,
    var parentWorkflowId: String,
    var isComplete: Boolean,
)