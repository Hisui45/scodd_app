package com.example.scodd.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.scodd.ui.dashboard.DashboardScreen
import com.example.scodd.ui.login.LoginScreen
import com.example.scodd.navigation.ScoddDestinationsArgs.CHORE_ID_ARG
import com.example.scodd.navigation.ScoddDestinationsArgs.TITLE_ARG
import com.example.scodd.ui.chore.*
import com.example.scodd.ui.mode.*

@Composable
fun ScoddNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AuthorizeNav.route,
        modifier = modifier,

    ) {

        authorizeGraph(navController)

        composable(route = DashboardNav.route) {
            DashboardScreen(
//                onClickSeeAllAccounts = {
//                    navController.navigateSingleTopTo(Accounts.route)
//                },
//                onClickSeeAllBills = {
//                    navController.navigateSingleTopTo(Bills.route)
//                },
//                onAccountClick = { accountType ->
//                    navController.navigateToSingleAccount(accountType)
//                }
            )
        }

        choreGraph(navController)

        modeGraph(navController)


//        composable(
//            route = SingleAccount.routeWithArgs,
//            arguments = SingleAccount.arguments,
//            deepLinks = SingleAccount.deepLinks
//        ) { navBackStackEntry ->
//            val accountType =
//                navBackStackEntry.arguments?.getString(SingleAccount.accountTypeArg)
//            SingleAccountScreen(accountType)
//        }
    }
}

fun NavGraphBuilder.authorizeGraph(navController: NavController){

    navigation(route = AuthorizeNav.route, startDestination = AuthorizeNav.Login.route){

        composable(route = AuthorizeNav.Login.route) {
            LoginScreen(
                onRegisterClick ={

                },
                onLoginClick = {
                    //After authorization succeeds
                    navController.navigate(DashboardNav.route)
                }
            )
        }

        composable(route = AuthorizeNav.Register.route){

        }

    }
}
fun NavGraphBuilder.choreGraph(navController: NavController){

    navigation(route = ChoreNav.route, startDestination = ChoreNav.Chores.route){

        composable(route = ChoreNav.Chores.route) {
            ChoreScreen(
                onCreateWorkflowClick = {
                    navController.navigate(ChoreNav.CreateWorkflow.route)
                },
                onCreateChoreClick = {
                    navController.navigate(ChoreNav.CreateChore.route)
                },
                onEditChore = { choreId ->
                    navController.navigate(
                        ChoreNav.CreateChore.route.let {
                                if (choreId != null) "$it?choreId=$choreId" else it
                            }
                    )
                },
                onWorkflowClick = {
                    navController.navigate(ChoreNav.Workflow.route + "/${it.title}")
                }
            )
        }

        composable(
            route = ChoreNav.CreateChore.routeWithArgs,
            arguments = listOf(
                navArgument("choreId") { type = NavType.StringType; nullable = true },
            )
        ){navBackStack ->
            val choreId = navBackStack.arguments?.getString("choreId")?.takeIf { it != "null" }
            CreateChoreScreen(
                onTaskUpdate = {
//                    navController.navigate(
//                        "$ADD_EDIT_TASK_SCREEN/$title".let {
//                            if (taskId != null) "$it?$TASK_ID_ARG=$taskId" else it
//                        }
//                    )
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = ChoreNav.CreateWorkflow.route){
            CreateWorkflowScreen(
                onAddChoreButtonClick = {navController.navigate(ChoreNav.SelectChore.route + "/{hey}")}
            )
        }

        composable(route = ChoreNav.SelectChore.route + "/{workflowID}"){ navBackStack ->
            val workflow = navBackStack.arguments?.getString("workflowID")
            SelectChoreScreen(
                onSelectFinish = {
                    //Take list
                    //Use workflow ID to add chores to
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                navigateToChoreCreate = {
                    navController.navigate(ChoreNav.CreateChore.route)
                }
            )
        }

        composable(route = ChoreNav.Workflow.route + "/{workflowID}") { navBackStack ->

            // Extracting the argument
            val workflow = navBackStack.arguments?.getString("workflowID")

            // Setting screen,
            // Pass the extracted Counter
            WorkflowScreen(
                workflowTitle = workflow,
                onNavigateBack = {navController.popBackStack()},
                onAddChoreClick = {navController.navigate(route = ChoreNav.SelectChore.route + "/${it.title}")} //Change to id
                )
        }

    }
}


fun NavGraphBuilder.modeGraph(navController: NavController){

    navigation(route = Mode.route, startDestination = Mode.Modes.route){

        composable(route = Mode.Modes.route) {
            ModeScreen(
                modeScreens = scoddModeScreens,
                onModeClick = { mode ->
                    navController.navigate(mode.route)
                }
            )
        }

        composable(route = Mode.TimeMode.route){
            TimeModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddChoreToModeClick = {
                    navController.navigate(ChoreNav.SelectChore.route + "/${it.title}") //Should send list object with selected chores
                }
            )
        }

        composable(route = Mode.QuestMode.route){
            QuestModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },

            )
        }

        composable(route = Mode.SpinMode.route){
            SpinModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddChoreToModeClick = {
                    navController.navigate(ChoreNav.SelectChore.route + "/${it.title}") //Should send list object with selected chores
                }
            )
        }

        composable(route = Mode.SandMode.route){
            SandModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddChoreToModeClick = {
                    navController.navigate(ChoreNav.SelectChore.route + "/${it.title}") //Should send list object with selected chores
                }
            )
        }

        composable(route = Mode.BankMode.route){
            BankModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddChoreToModeClick = {
                    navController.navigate(ChoreNav.SelectChore.route + "/${it.title}") //Should send list object with selected chores
                }
            )
        }
    }
}
fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }

//private fun NavHostController.navigateToSingleAccount(accountType: String) {
//    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
//}
