package com.example.scodd.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.scodd.navigation.*
import com.example.scodd.navigation.ScoddBottomBar
import com.example.scodd.ui.theme.ScoddTheme
import org.junit.Rule
import org.junit.Test


class BottomNavigationBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun scoddBottomNavigationBarTest_shouldDisplayCorrectItems() {
        composeTestRule.setContent {
            val navScreens = scoddBottomNavScreens
                ScoddTheme {
                    ScoddBottomBar(
                        bottomNavScreens = navScreens,
                        onNavSelected = {},
                        currentScreen = DashboardNav
                    )

            }
        }

        // Verify that the correct bottom nav items and their icons are displayed
        composeTestRule.onNodeWithText(DashboardNav.label).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = DashboardNav.label, useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithText(ChoreNav.Chores.label).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = ChoreNav.Chores.label, useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithText(ModeNav.Modes.label).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = ModeNav.Modes.label, useUnmergedTree = true).assertIsDisplayed()
    }
    
    @Test
    fun navigationBar_shouldSelectWhenClicked() {
        var selectedItem: ScoddBottomNavDestination = ChoreNav.Chores
        val navScreens = scoddBottomNavScreens
        composeTestRule.setContent {
            ScoddTheme{
                ScoddBottomBar(
                    bottomNavScreens = navScreens,
                    onNavSelected = {selectedItem = it},
                    currentScreen = selectedItem
                )
            }
        }

        // Verify that correct bottom nav item is selected when clicked
        composeTestRule.onNodeWithText(DashboardNav.label).performClick()
        assert(selectedItem.route == DashboardNav.route)

        composeTestRule.onNodeWithText(ChoreNav.Chores.label).performClick()
        assert(selectedItem.route == ChoreNav.Chores.route)

        composeTestRule.onNodeWithText(ModeNav.Modes.label).performClick()
        assert(selectedItem.route == ModeNav.Modes.route)
    }

//    @Test
//    fun navigationBar_shouldSelectWhenClicked() {
//        var selectedItem: ScoddBottomNavDestination = ChoreNav.Chores
//        val navScreens = scoddBottomNavScreens
//        composeTestRule.setContent {
//            ScoddTheme{
//                ScoddBottomBar(
//                    bottomNavScreens = navScreens,
//                    onNavSelected = {selectedItem = it},
//                    currentScreen = selectedItem
//                )
//            }
//        }
//
//        // Verify that correct bottom nav item is selected when clicked
//        composeTestRule.onNodeWithText(DashboardNav.label).performClick()
//        assert(selectedItem == DashboardNav)
//
//        composeTestRule.onNodeWithText(ChoreNav.Chores.label).performClick()
//        assert(selectedItem == ChoreNav.Chores)
//
//        composeTestRule.onNodeWithText(ModeNav.Modes.label).performClick()
//        assert(selectedItem == ModeNav.Modes)
//    }

}
