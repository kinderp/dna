package org.traveldna.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivitySmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreenShowsPilotIdentity() {
        composeRule.onNodeWithText("Travel DNA").assertIsDisplayed()
        composeRule.onNodeWithText("Pilot 0 · shell Android didattica").assertIsDisplayed()
    }
}
