package com.example.scodd

interface ScoddDestination {
    val icon: Int
    val route: String

}
    /**
     * Scodd app navigation destinations
     */
    object Dashboard : ScoddDestination {
        override val icon = R.drawable.window_24
        override val route = "Dashboard"
    }

    object Chores : ScoddDestination {
        override val icon =  R.drawable.mop_24
        override val route = "Chores"
    }

    object Modes : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Modes"
    }

    val scoddScreens = listOf(Chores, Dashboard, Modes)
