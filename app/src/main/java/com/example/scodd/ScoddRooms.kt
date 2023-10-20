package com.example.scodd

interface ScoddRoom {
    val title : String
}

object Room1 : ScoddRoom{
    override val title = "Kitchen"
}

object Room2 : ScoddRoom{
    override val title = "Bedroom"
}

object Room3 : ScoddRoom{
    override val title = "Living Room"
}

object Room4 : ScoddRoom{
    override val title = "Bathroom"
}

object Room5 : ScoddRoom{
    override val title = "Personal"
}

object Room6 : ScoddRoom{
    override val title = "Home Office"
}

object Room7 : ScoddRoom{
    override val title = "Favorites"
}


val scoddRooms = listOf(Room1,Room2, Room3, Room4, Room5, Room6, Room7)