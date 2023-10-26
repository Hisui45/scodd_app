package com.example.scodd.objects

interface ScoddRoom {
    val title : String
    val selected : Boolean
}


//object Room0 : ScoddRoom{
//    override val title = "All"
//    override var selected = true
//}

object Room1 : ScoddRoom {
    override val title = "Kitchen"
    override val selected = false
}

object Room2 : ScoddRoom {
    override val title = "Bedroom"
    override val selected = false
}

object Room3 : ScoddRoom {
    override val title = "Living Room"
    override val selected = false
}

object Room4 : ScoddRoom {
    override val title = "Bathroom"
    override val selected = false
}

object Room5 : ScoddRoom {
    override val title = "Personal"
    override val selected = false
}

object Room6 : ScoddRoom {
    override val title = "Home Office"
    override val selected = false
}

object Room7 : ScoddRoom {
    override val title = "Favorites"
    override val selected = false
}


val scoddRooms = listOf(Room1, Room2, Room3, Room4, Room5, Room6, Room7)