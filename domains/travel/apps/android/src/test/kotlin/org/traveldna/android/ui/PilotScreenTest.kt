package org.traveldna.android.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class PilotScreenTest {
    @Test
    fun everyRouteRoundTrips() {
        PilotScreen.entries.forEach { screen ->
            assertEquals(screen, PilotScreen.fromSavedRoute(screen.route))
        }
    }

    @Test
    fun missingOrUnknownRouteFallsBackToHome() {
        assertEquals(PilotScreen.Home, PilotScreen.fromSavedRoute(null))
        assertEquals(PilotScreen.Home, PilotScreen.fromSavedRoute("unknown"))
    }

    @Test
    fun routesAndSemanticTagsMatchPersistedContract() {
        assertEquals(
            listOf("home", "pilots", "demo", "study"),
            PilotScreen.entries.map { it.route },
        )
        assertEquals(
            listOf(
                "pilot-navigation-home",
                "pilot-navigation-pilots",
                "pilot-navigation-demo",
                "pilot-navigation-study",
            ),
            PilotScreen.entries.map { it.navigationTag },
        )
        assertEquals(
            listOf(
                "pilot-screen-home",
                "pilot-screen-pilots",
                "pilot-screen-demo",
                "pilot-screen-study",
            ),
            PilotScreen.entries.map { it.contentTag },
        )
    }

    @Test
    fun semanticTagsAreUnique() {
        assertEquals(PilotScreen.entries.size, PilotScreen.entries.map { it.navigationTag }.toSet().size)
        assertEquals(PilotScreen.entries.size, PilotScreen.entries.map { it.contentTag }.toSet().size)
    }
}
