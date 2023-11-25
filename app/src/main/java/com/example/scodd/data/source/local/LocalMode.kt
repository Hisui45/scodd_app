package com.example.scodd.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "mode"
)
data class LocalMode(
    @PrimaryKey val id: String,
    val workflowChores: List<String>,
    val selectedWorkflows: List<String>,
    val chores: List<String>,
    val rooms: List<String>,
)