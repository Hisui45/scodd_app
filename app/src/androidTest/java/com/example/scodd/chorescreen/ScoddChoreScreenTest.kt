package com.example.scodd.chorescreen

import com.example.scodd.HiltTestActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.scodd.data.ChoreRepository
import com.example.scodd.navigation.ChoreNav
import com.example.scodd.ui.chore.ChoreScreen
import com.example.scodd.ui.chore.ChoreViewModel
import com.example.scodd.ui.theme.ScoddTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
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
class ScoddChoreTest{

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
    fun scoddNavHost_verifyStartDestination() {
        assertEquals(ChoreNav.Chores.label, navController.currentBackStackEntry?.destination?.route)
    }
    

    private fun setContent(choreRepository: ChoreRepository) {
        composeTestRule.setContent {
            ScoddTheme {
                ChoreScreen(
                    onCreateWorkflowClick = {},
                    onCreateChoreClick = {},
                    onEditChore = {},
                    onViewWorkflow = {},
                    viewModel = ChoreViewModel(choreRepository = choreRepository,
                                                savedStateHandle = SavedStateHandle()
                    )
                )
            }
        }
    }

}