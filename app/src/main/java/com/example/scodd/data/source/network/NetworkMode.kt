package com.example.scodd.data.source.network


data class NetworkMode(
    val id: String,
    val workflowChores: List<String>,
    val selectedWorkflows: List<String>,
    val chores: List<String>,
    val rooms: List<String>,
)
