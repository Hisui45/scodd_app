package com.example.scodd.model

import androidx.compose.runtime.Immutable

@Immutable
data class Room(
    val id: String,
    val title : String,
    val selected: Boolean = false
)