package com.example.scodd.objects

import com.example.scodd.R

interface ScoddDestination {
    val icon: Int
    val route: String
    val parentRoute : String

}
/**
 * Scodd app navigation destinations
 */

object Authorize : ScoddDestination{
    override val icon = R.drawable.mop_24
    override val route = "Authorize"
    override val parentRoute = ""

    object Login : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Login"
        override val parentRoute = "Authorize"
    }
    object Register : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Register"
        override val parentRoute = "Authorize"
    }
}

object Dashboard : ScoddDestination {
    override val icon = R.drawable.window_24
    override val route = "Dashboard"
    override val parentRoute = ""
}

object Chore : ScoddDestination {
    override val icon = R.drawable.mop_24
    override val route = "Chore"
    override val parentRoute = ""

    object Chores : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Chores"
        override val parentRoute = ""
    }
    object CreateWorkflow : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "New Workflow"
        override val parentRoute = "Chores"
    }

    object SelectChore : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Select Chores"
        override val parentRoute = "Chores"
    }

    object Workflow : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Workflow"
        override val parentRoute = "Chores"
    }

    object CreateChore : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "New Chore"
        override val parentRoute = "Chores"
    }
}

object Mode : ScoddDestination {
    override val icon = R.drawable.burst_mode_24
    override val route = "Mode"
    override val parentRoute = ""

    object Modes : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Modes"
        override val parentRoute = ""
    }
    object TimeMode : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Time Crunch"
        override val parentRoute = "Modes"
    }

    object QuestMode : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Chore Quest"
        override val parentRoute = "Modes"
    }

    object SpinMode : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Chore Spin"
        override val parentRoute = "Modes"
    }

    object SandMode : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Sand Glass"
        override val parentRoute = "Modes"
    }

    object BankMode : ScoddDestination {
        override val icon = R.drawable.burst_mode_24
        override val route = "Piggy Bank"
        override val parentRoute = "Modes"
    }
}

val scoddScreens = listOf(Chore.Chores, Dashboard, Mode.Modes)
val scoddChoreScreens = listOf(Chore.CreateChore,Chore.CreateWorkflow)
val scoddModeScreens = listOf(Mode.TimeMode, Mode.SpinMode,Mode.BankMode, Mode.QuestMode, Mode.SandMode)
//val scoddWorkflowScreens = listOf()
