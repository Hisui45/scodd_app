package com.example.scodd.objects

interface ScoddRoom {
    val title : String
    var selected : Boolean
}


//object Room0 : ScoddRoom{
//    override val title = "All"
//    override var selected = true
//}

object Room1 : ScoddRoom {
    override val title = "Kitchen"
    override var selected = false
}

object Room2 : ScoddRoom {
    override val title = "Bedroom"
    override var selected = true
}

object Room3 : ScoddRoom {
    override val title = "Living Room"
    override var selected = false
}

object Room4 : ScoddRoom {
    override val title = "Bathroom"
    override var selected = false
}

object Room5 : ScoddRoom {
    override val title = "Personal"
    override var selected = false
}

object Room6 : ScoddRoom {
    override val title = "Home Office"
    override var selected = false
}

object Room7 : ScoddRoom {
    override val title = "Favorites"
    override var selected = false
}


val scoddRooms = listOf(Room1, Room2, Room3, Room4, Room5, Room6, Room7)