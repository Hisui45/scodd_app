package com.example.scodd

interface ScoddChore {
    val title : String
    val room : ScoddRoom
    val favorites : Boolean
}

object Chore1 : ScoddChore{
    override val title = "Wash Dishes"
    override val room = Room1
    override val favorites =  false
}

object Chore2 : ScoddChore{
    override val title = "Make Bed"
    override val room = Room2
    override val favorites =  true
}

object Chore3 : ScoddChore{
    override val title = "Wash Clothes"
    override val room = Room2
    override val favorites =  true
}

object Chore4 : ScoddChore{
    override val title = "Vacuum"
    override val room = Room3
    override val favorites =  false
}

object Chore5 : ScoddChore{
    override val title = "Clean Toilet"
    override val room = Room4
    override val favorites =  false
}

object Chore6 : ScoddChore{
    override val title = "Brush Teeth"
    override val room = Room5
    override val favorites =  false
}

object Chore7 : ScoddChore{
    override val title = "Shred Papers"
    override val room = Room6
    override val favorites =  false
}


val scoddChores = listOf(Chore1,Chore2, Chore3, Chore4, Chore5, Chore6, Chore7)