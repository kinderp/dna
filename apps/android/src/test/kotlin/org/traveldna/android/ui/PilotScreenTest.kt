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
    fun semanticTagsAreStableAndUnique() {
        assertEquals(PilotScreen.entries.size, PilotScreen.entries.map { it.navigationTag }.toSet().size)
        assertEquals(PilotScreen.entries.size, PilotScreen.entries.map { it.contentTag }.toSet().size)
    }
}
