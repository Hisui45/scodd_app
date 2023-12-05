package com.example.scodd.model

import androidx.compose.runtime.Immutable

@Immutable
data class User(
    val id: String,
    val name: String,
    val bankModeValue: Int,
    val lastUpdated: Long
)