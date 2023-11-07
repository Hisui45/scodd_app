package com.example.scodd

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.scodd.chore.*
import com.example.scodd.dashboard.DashboardScreen
import com.example.scodd.login.LoginScreen
import com.example.scodd.mode.*
import com.example.scodd.objects.*

@Composable
fun ScoddNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    focusManager : FocusManager
) {
    NavHost(
        navController = navController,
        startDestination = Authorize.route,
        modifier = modifier,

    ) {

        authorizeGraph(navController)

        composable(route = Dashboard.route) {
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

        choreGraph(navController, focusManager)

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

    navigation(route = Authorize.route, startDestination = Authorize.Login.route){

        composable(route = Authorize.Login.route) {
            LoginScreen(
                onRegisterClick ={

                },
                onLoginClick = {
                    //After authorization succeeds
                    navController.navigate(Dashboard.route)
                }
            )
        }

        composable(route = Authorize.Register.route){

        }

    }
}
fun NavGraphBuilder.choreGraph(navController: NavController, focusManager: FocusManager){

    navigation(route = Chore.route, startDestination = Chore.Chores.route){

        composable(route = Chore.Chores.route) {
            ChoreScreen(
                onCreateWorkflowClick = {
                    navController.navigate(Chore.CreateWorkflow.route)
                },
                onCreateChoreClick = {
                    navController.navigate(Chore.CreateChore.route)
                },
                onWorkflowClick = {
                    navController.navigate(Chore.Workflow.route + "/${it.title}")
                }
            )
        }

        composable(route = Chore.CreateChore.route){
            CreateChoreScreen(focusManager)
        }

        composable(route = Chore.CreateWorkflow.route){
            CreateWorkflowScreen(
                onAddChoreButtonClick = {navController.navigate(Chore.SelectChore.route + "/{hey}")}
            )
        }

        composable(route = Chore.SelectChore.route + "/{workflowID}"){ navBackStack ->
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
                    navController.navigate(Chore.CreateChore.route)
                }
            )
        }

        composable(route = Chore.Workflow.route + "/{workflowID}") { navBackStack ->

            // Extracting the argument
            val workflow = navBackStack.arguments?.getString("workflowID")

            // Setting screen,
            // Pass the extracted Counter
            WorkflowScreen(
                workflowTitle = workflow,
                onNavigateBack = {navController.popBackStack()},
                onAddChoreClick = {navController.navigate(route = Chore.SelectChore.route + "/${it.title}")} //Change to id
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
                    navController.navigate(Chore.SelectChore.route + "/${it.title}") //Should send list object with selected chores
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
                    navController.navigate(Chore.SelectChore.route + "/${it.title}") //Should send list object with selected chores
                }
            )
        }

        composable(route = Mode.SandMode.route){
            SandModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddChoreToModeClick = {
                    navController.navigate(Chore.SelectChore.route + "/${it.title}") //Should send list object with selected chores
                }
            )
        }

        composable(route = Mode.BankMode.route){
            BankModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddChoreToModeClick = {
                    navController.navigate(Chore.SelectChore.route + "/${it.title}") //Should send list object with selected chores
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
