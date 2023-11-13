package com.example.scodd.utils

interface ScoddSuggestions {
    val title : String
}


object ADHD : ScoddSuggestions {
    override val title = "ADHD"
}

object Game : ScoddSuggestions {
    override val title = "Game"
}

object Motivation : ScoddSuggestions {
    override val title = "Motivation"
}