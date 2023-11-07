package com.example.scodd.objects

interface ScoddChore {
    val title : String
    val room : ScoddRoom
    val timerValue : Int
    val bankValue : Int
    val favorite : Boolean
}

object Chore1 : ScoddChore {
    override val title = "Wash Dishes and Clean Stove and Wipe Counter"
    override val room = Room1
    override val timerValue = 8
    override val bankValue = 10
    override val favorite =  false
}

object Chore2 : ScoddChore {
    override val title = "Make Bed"
    override val room = Room2
    override val timerValue = 2
    override val bankValue = 1
    override val favorite =  true
}

object Chore3 : ScoddChore {
    override val title = "Wash Clothes"
    override val room = Room2
    override val timerValue = 5
    override val bankValue = 5
    override val favorite =  true
}

object Chore4 : ScoddChore {
    override val title = "Vacuum"
    override val room = Room3
    override val timerValue = 12
    override val bankValue = 5
    override val favorite =  false
}

object Chore5 : ScoddChore {
    override val title = "Clean Toilet"
    override val room = Room4
    override val timerValue = 4
    override val bankValue = 8
    override val favorite =  false
}

object Chore6 : ScoddChore {
    override val title = "Brush Teeth"
    override val room = Room5
    override val timerValue = 3
    override val bankValue = 2
    override val favorite =  false
}

object Chore7 : ScoddChore {
    override val title = "Shred Papers"
    override val room = Room6
    override val timerValue = 15
    override val bankValue = 6
    override val favorite =  false
}


val scoddChores = listOf(Chore1, Chore2, Chore3, Chore4, Chore5, Chore6, Chore7)