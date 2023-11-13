package com.example.scodd.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "room"
)
data class LocalRoom(
    @PrimaryKey val id: String,
    var title: String,
    val selected: Boolean
)