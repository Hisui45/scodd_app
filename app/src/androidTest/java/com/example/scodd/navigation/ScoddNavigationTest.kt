package com.example.scodd.navigation

import com.example.scodd.HiltTestActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.scodd.data.ChoreRepository
import com.example.scodd.R
import com.example.scodd.ScoddApp
import com.example.scodd.ScoddApplication
import com.example.scodd.ui.theme.ScoddTheme
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


/**
 * Tests for scenarios that requires navigating within the app.
 */


@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class ScoddNavigationTest{

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    private val activity get() = composeTestRule.activity

    @Inject
    lateinit var choreRepository: ChoreRepository

    private lateinit var navController: TestNavHostController
    @Before
    fun init() {
        hiltRule.inject()
    }

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
        composeTestRule.onNodeWithContentDescription(label = DashboardNav.route, useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithText(ChoreNav.Chores.label).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = ChoreNav.Chores.route, useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithText(ModeNav.Modes.label).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(label = ModeNav.Modes.route, useUnmergedTree = true).assertIsDisplayed()
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
        assert(selectedItem == DashboardNav)

        composeTestRule.onNodeWithText(ChoreNav.Chores.label).performClick()
        assert(selectedItem == ChoreNav.Chores)

        composeTestRule.onNodeWithText(ModeNav.Modes.label).performClick()
        assert(selectedItem == ModeNav.Modes)
    }

    @Test
    fun bottomNavigationBetweenScreens() {
        setContent()

        navigateToChoreScreen()

        // Start the dashboard screen
                composeTestRule.onNodeWithContentDescription(label = DashboardNav.route, useUnmergedTree = true)
                    .performClick()
        // Check that dashboard screen was opened.
        composeTestRule.onNodeWithText(activity.getString(R.string.roundup_header))
            .assertIsDisplayed()

        // Start the Modes screen
        composeTestRule.onNodeWithContentDescription(label = ModeNav.Modes.route, useUnmergedTree = true)
            .performClick()
        // Check that dashboard screen was opened.
        composeTestRule.onNodeWithTag(activity.getString(R.string.modes_title))
            .assertIsDisplayed()
    }

    @Test
    fun choreScreen_FABToCreateChore() {
        setContent()

        navigateToChoreScreen()

        //Click on FAB
        composeTestRule.onNodeWithContentDescription(label = activity.getString(R.string.fab_create), useUnmergedTree = true)
            .performClick()

        //Click on Create FAB button
        composeTestRule.onNodeWithText(text = activity.getString(R.string.chore_title), useUnmergedTree = true)
            .performClick()

        Thread.sleep(5000)

//        // Check that create chore screen was opened.
//        composeTestRule.onNodeWithTag("ChoreScreen", useUnmergedTree = true)
//            .assertIsDisplayed()
    }


    private fun setContent() {
        composeTestRule.setContent {
            ScoddTheme {
                ScoddApp()
            }
        }
    }

    private fun navigateToChoreScreen() {
        // Start dashboard screen.
        composeTestRule.onNodeWithText(activity.getString(R.string.log_in_button)).performClick()
        // Check that dashboard screen was opened.
        composeTestRule.onNodeWithText(activity.getString(R.string.roundup_header))
            .assertIsDisplayed()


        // Start chores screen.
        composeTestRule.onNodeWithContentDescription(label = ChoreNav.Chores.route, useUnmergedTree = true)
            .performClick()
        // Check that tasks screen was opened.
        composeTestRule.onNodeWithText(activity.getString(R.string.workflow_label))
            .assertIsDisplayed()
    }
}