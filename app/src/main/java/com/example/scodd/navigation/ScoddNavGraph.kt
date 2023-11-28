package com.example.scodd.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scodd.ui.dashboard.DashboardScreen
import com.example.scodd.ui.login.LoginScreen
import com.example.scodd.ui.chore.*
import com.example.scodd.ui.mode.*
import com.example.scodd.ui.workflow.CreateWorkflowScreen
import com.example.scodd.ui.workflow.SelectChoreScreen
import com.example.scodd.ui.workflow.WorkflowScreen
import com.google.gson.Gson

@Composable
fun ScoddNavGraph(
    navController: NavHostController = rememberNavController(),
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
                            "$it?choreId=$choreId"
                            }
                    )
                },
                onViewWorkflow = { workflowId ->
                    navController.navigate(
                        ChoreNav.Workflow.route.let {
                            "$it?workflowId=$workflowId"
                        }
                    )
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
                choreId = choreId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = ChoreNav.CreateWorkflow.route,){ navBackStack ->
            val selectedItems =
                navBackStack.savedStateHandle.
                getStateFlow<List<String>?>("selectedChores", null).collectAsState()
            navBackStack.savedStateHandle.remove<List<String>>("selectedChores")
            CreateWorkflowScreen(
                selectedItems = selectedItems,
                onNavigateBack = {navController.popBackStack()},
                onAddChoreButtonClick = { incomingSelectedChores ->
                    val converted = Gson().toJson(incomingSelectedChores)
                    navController.navigate(
                        ChoreNav.SelectChore.route + "/$converted" + "/" + null
                        )}
            )
        }

        composable(route = ChoreNav.SelectChore.routeWithArgs,
            arguments = listOf(
                navArgument("incomingSelectedChores") { type = NavType.StringType},
                navArgument("workflowId") { type = NavType.StringType; nullable = true }
            )
        ){ navBackStack ->
            val incomingSelectedChores = navBackStack.arguments?.getString("incomingSelectedChores")
            val workflowId = navBackStack.arguments?.getString("workflowId")

            if (incomingSelectedChores != null) {
                val converted = Gson().fromJson(incomingSelectedChores, Array<String>::class.java).toList()
                SelectChoreScreen(
                    incomingSelectedChores = converted,
                    workflowId = workflowId,
                    onSelectFinish = { data ->
                        // Pass data back to A
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selectedChores", data)
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
        }

        composable(
            route = ChoreNav.Workflow.routeWithArgs,
            arguments = listOf(
                navArgument("workflowId") { type = NavType.StringType},
            )) { navBackStack ->
            val selectedItems =
                navBackStack.savedStateHandle.
                getStateFlow<List<String>?>("selectedChores", null).collectAsState()
            navBackStack.savedStateHandle.remove<List<String>>("selectedChores")
            WorkflowScreen(
                selectedItems = selectedItems,
                onNavigateBack = {navController.popBackStack()},
                onAddChoreClick = { workflowId, incomingSelectedChores ->
                    val converted = Gson().toJson(incomingSelectedChores)
                    navController.navigate(
                        ChoreNav.SelectChore.route + "/$converted" + "/$workflowId"
                    )}
                )
        }

    }
}


fun NavGraphBuilder.modeGraph(navController: NavController){

    navigation(route = ModeNav.route, startDestination = ModeNav.Modes.route){

        composable(route = ModeNav.Modes.route) {
            ModeScreen(
                onModeClick = { mode ->
                    navController.navigate(
                        ModeNav.StartMode.route.let {
                            "$it?modeId=${mode.modeId}"
                        }
                    )
                }
            )
        }

        composable(route = ModeNav.StartMode.routeWithArgs, arguments = listOf(
            navArgument("modeId") { type = NavType.StringType}
        )
        ){navBackStack ->
            val selectedItems =
                navBackStack.savedStateHandle.
                getStateFlow<List<String>?>("selectedChores", null).collectAsState()
            navBackStack.savedStateHandle.remove<List<String>>("selectedChores")
            StartModeScreen(
                selectedItems = selectedItems,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditChore = { choreId ->
                    navController.navigate(
                        ChoreNav.CreateChore.route.let {
                            "$it?choreId=$choreId"
                        }
                    )
                },
                onAddChoreClick = { incomingSelectedChores ->
                    val converted = Gson().toJson(incomingSelectedChores)
                    navController.navigate(
                        ChoreNav.SelectChore.route + "/$converted" + "/" + null
                    )},
                onStartClick = { incomingSelectedChores, modeId, timeDuration ->
                        val convertChores = Gson().toJson(incomingSelectedChores)
//                        val convertDuration = Gson().toJson(timeDuration)
                        navController.navigate(
                            ModeNav.ProgressMode.route + "/$convertChores" + "/$modeId" + "/$timeDuration"
                        )
                }
            )
        }

        composable(route = ModeNav.ProgressMode.routeWithArgs,
            arguments = listOf(
                navArgument("incomingSelectedChores") { type = NavType.StringType},
                navArgument("modeId") { type = NavType.StringType},
                navArgument("timeDuration") {type = NavType.LongType}
            )
        ){ navBackStack ->
            val incomingSelectedChores = navBackStack.arguments?.getString("incomingSelectedChores")
            val modeId = navBackStack.arguments?.getString("modeId")

            if (incomingSelectedChores != null) {
                val converted = Gson().fromJson(incomingSelectedChores, Array<String>::class.java).toList()
                CompleteModeScreen(
                    incomingSelectedItems = converted,
                    modeId = modeId,
                    onSelectFinish = { data ->
                        // Pass data back to A
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selectedChores", data)
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
        }


//        composable(route = ModeNav.TimeMode.route){navBackStack ->
//            val selectedItems =
//                navBackStack.savedStateHandle.
//                getStateFlow<List<String>?>("selectedChores", null).collectAsState()
//            navBackStack.savedStateHandle.remove<List<String>>("selectedChores")
//            TimeModeScreen(
//                selectedItems = selectedItems,
//                onNavigateBack = {
//                    navController.popBackStack()
//                },
//                onEditChore = { choreId ->
//                    navController.navigate(
//                        ChoreNav.CreateChore.route.let {
//                            "$it?choreId=$choreId"
//                        }
//                    )
//                },
//                onAddChoreClick = { incomingSelectedChores ->
//                    val converted = Gson().toJson(incomingSelectedChores)
//                    navController.navigate(
//                        ChoreNav.SelectChore.route + "/$converted" + "/" + null
//                    )}
//            )
//        }
//
//        composable(route = ModeNav.QuestMode.route){
//            QuestModeScreen(
//                onNavigateBack = {
//                    navController.popBackStack()
//                },
//
//            )
//        }
//
//        composable(route = ModeNav.SpinMode.route){navBackStack ->
//            val selectedItems =
//                navBackStack.savedStateHandle.
//                getStateFlow<List<String>?>("selectedChores", null).collectAsState()
//            navBackStack.savedStateHandle.remove<List<String>>("selectedChores")
//            SpinModeScreen(
//                selectedItems = selectedItems,
//                onNavigateBack = {
//                    navController.popBackStack()
//                },
//                onAddChoreClick = { incomingSelectedChores ->
//                    val converted = Gson().toJson(incomingSelectedChores)
//                    navController.navigate(
//                        ChoreNav.SelectChore.route + "/$converted" + "/" + null
//                    )}
//            )
//        }
//
//        composable(route = ModeNav.SandMode.route){navBackStack ->
//            val selectedItems =
//                navBackStack.savedStateHandle.
//                getStateFlow<List<String>?>("selectedChores", null).collectAsState()
//            navBackStack.savedStateHandle.remove<List<String>>("selectedChores")
//            SandModeScreen(
//                selectedItems = selectedItems,
//                onNavigateBack = {
//                    navController.popBackStack()
//                },
//                onAddChoreClick = { incomingSelectedChores ->
//                    val converted = Gson().toJson(incomingSelectedChores)
//                    navController.navigate(
//                        ChoreNav.SelectChore.route + "/$converted" + "/" + null
//                    )}
//            )
//        }
//
//        composable(route = ModeNav.BankMode.route){navBackStack ->
//            val selectedItems =
//                navBackStack.savedStateHandle.
//                getStateFlow<List<String>?>("selectedChores", null).collectAsState()
//            navBackStack.savedStateHandle.remove<List<String>>("selectedChores")
//            BankModeScreen(
//                selectedItems = selectedItems,
//                onNavigateBack = {
//                    navController.popBackStack()
//                },
//                onEditChore = { choreId ->
//                    navController.navigate(
//                        ChoreNav.CreateChore.route.let {
//                            "$it?choreId=$choreId"
//                        }
//                    )
//                },
//                onAddChoreClick = { incomingSelectedChores ->
//                    val converted = Gson().toJson(incomingSelectedChores)
//                    navController.navigate(
//                        ChoreNav.SelectChore.route + "/$converted" + "/" + null
//                    )}
//            )
//        }
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
