package com.example.scodd.navigation

import com.example.scodd.R

interface ScoddBottomNavDestination {
    val icon: Int
    val route: String
    val label: String
    val args: String
        get() = ""
}

interface ScoddDestination {
    val route: String
    val routeWithArgs: String
        get() = ""
    val parentRoute : String
    val label: String
        get() = ""
}
/**
 * Scodd app navigation destinations
 */

object ScoddDestinationsArgs {
    const val USER_MESSAGE_ARG = "userMessage"
    const val CHORE_ID_ARG = "choreId"
    const val TITLE_ARG = "title"
}

object AuthorizeNav : ScoddDestination {
    override val route = "Authorize"
    override val parentRoute = ""

    object Login : ScoddDestination {
        override val route = "Login"
        override val parentRoute = "Authorize"
    }
    object Register : ScoddDestination {
        override val route = "Register"
        override val parentRoute = "Authorize"
    }
}

object DashboardNav : ScoddBottomNavDestination {
    override val icon = R.drawable.window_24
    override val route = "dashboard_screen"
    override val label = "Dashboard"
}

object ChoreNav : ScoddBottomNavDestination {
    override val icon = R.drawable.mop_24
    override val route = "chore_nav"
    override val label = "Chore"

    object Chores : ScoddBottomNavDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "chores_screen"
        override val label = "Chores"
    }
    object CreateWorkflow : ScoddDestination {
        override val route = "create_workflow_screen"
        override val parentRoute = "chores_screen"
    }

    object SelectChore : ScoddDestination {
        override val route = "select_chores_screen"
        override val parentRoute = "chores_screen"
    }

    object Workflow : ScoddDestination {
        override val route = "workflow_screen"
        override val parentRoute = "chores_screen"
    }

    object CreateChore : ScoddDestination {
        override val route = "new_chore"
        override val routeWithArgs = "$route?choreId={choreId}"
        override val parentRoute = "chores_screen"
    }
}
object Mode : ScoddBottomNavDestination {
    override val icon = R.drawable.burst_mode_24
    override val route = "mode_nav"
    override val label = "Mode"

    object Modes : ScoddBottomNavDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "modes_screen"
        override val label = "Modes"
    }
    object TimeMode : ScoddDestination {
        override val route = "time_crunch_screen"
        override val parentRoute = "modes_screen"
        override val label = "Time Crunch"
    }

    object QuestMode : ScoddDestination {
        override val route = "chore_quest_screen"
        override val parentRoute = "modes_screen"
        override val label = "Chore Quest"
    }

    object SpinMode : ScoddDestination {
        override val route = "chore_spin_screen"
        override val parentRoute = "modes_screen"
        override val label = "Chore Spin"
    }

    object SandMode : ScoddDestination {
        override val route = "sand_glass_screen"
        override val parentRoute = "modes_screen"
        override val label = "Sand Glass"
    }

    object BankMode : ScoddDestination {
        override val route = "piggy_bank_screen"
        override val parentRoute = "modes_screen"
        override val label = "Bank Mode"
    }
}

val scoddBottomNavScreens = listOf(ChoreNav.Chores, DashboardNav, Mode.Modes)
val scoddChoreScreens = listOf(ChoreNav.CreateChore, ChoreNav.CreateWorkflow)
val scoddModeScreens = listOf(Mode.TimeMode, Mode.SpinMode, Mode.BankMode, Mode.QuestMode, Mode.SandMode)
