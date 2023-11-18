package com.example.scodd.model

data class Mode(
    val mode: String = "",
    val workflows: List<String> = emptyList(),
    val chores: List<String> = emptyList(),
    val rooms: List<String> = emptyList()
)

