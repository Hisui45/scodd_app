package com.example.scodd.model

data class ChoresGroup(
    val highlightedChore: Chore,
    val dashboardChores: List<Chore>,
    val bankChores: List<Chore>,
    val questChores: List<Chore>,
    val sandChores: List<Chore>,
    val spinChores: List<Chore>,
    val timeChores: List<Chore>
) {
    
    val allChores: List<Chore> =
        listOf(highlightedChore) + bankChores + questChores + sandChores + spinChores + sandChores + timeChores
}