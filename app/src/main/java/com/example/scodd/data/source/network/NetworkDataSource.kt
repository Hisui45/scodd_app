package com.example.scodd.data.source.network

interface NetworkDataSource {

    suspend fun loadChores(): List<NetworkChore>

    suspend fun saveChores(chores: List<NetworkChore>)

    suspend fun loadRooms(): List<NetworkRoom>

    suspend fun saveRooms(chores: List<NetworkRoom>)

    suspend fun loadWorkflows(): List<NetworkWorkflow>

    suspend fun saveWorkflows(chores: List<NetworkWorkflow>)

    suspend fun loadChoreItems(): List<NetworkChoreItem>

    suspend fun saveChoreItems(chores: List<NetworkChoreItem>)

    suspend fun loadModes(): List<NetworkMode>

    suspend fun saveModes(chores: List<NetworkMode>)

    suspend fun loadUser(): List<NetworkUser>

    suspend fun saveUser(chores: List<NetworkUser>)

}