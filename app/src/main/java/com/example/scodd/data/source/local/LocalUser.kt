package com.example.scodd.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user"
)
data class LocalUser(
    @PrimaryKey val id: String,
    var name: String,
    var bankModeValue: Int,
    var lastUpdated: Long
)