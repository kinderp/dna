package org.traveldna.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.traveldna.android.ui.PilotScreen

@RunWith(AndroidJUnit4::class)
class MainActivitySmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreenShowsPilotIdentity() {
        composeRule.onNodeWithText("Travel DNA").assertIsDisplayed()
        composeRule.onNodeWithText("Pilot 0 · shell Android didattica").assertIsDisplayed()
        composeRule.onNodeWithTag(PilotScreen.Home.contentTag).assertIsDisplayed()
    }

    @Test
    fun everyDeclaredDestinationIsReachableThroughSemantics() {
        PilotScreen.entries.forEach { screen ->
            composeRule.onNodeWithTag(screen.navigationTag).performClick()
            composeRule.onNodeWithTag(screen.contentTag).assertIsDisplayed()
        }
    }

    @Test
    fun selectedDestinationSurvivesActivityRecreation() {
        composeRule.onNodeWithTag(PilotScreen.Demo.navigationTag).performClick()
        composeRule.onNodeWithTag(PilotScreen.Demo.contentTag).assertIsDisplayed()

        composeRule.activityRule.scenario.recreate()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(PilotScreen.Demo.contentTag).assertIsDisplayed()
    }
}
