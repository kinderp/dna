package org.traveldna.android.pilot

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PilotCatalogTest {
    @Test
    fun `catalog keeps three ordered pilot stages`() {
        assertEquals(
            listOf(PilotStage.TeachingShell, PilotStage.RoadCompanion, PilotStage.ClosedBeta),
            PilotCatalog.milestones.map(PilotMilestone::stage),
        )
    }

    @Test
    fun `pilot zero remains permission-free and non-road`() {
        val pilotZero = PilotCatalog.milestones.first()
        assertTrue(pilotZero.nonGoals.contains("GPS reale"))
        assertTrue(pilotZero.nonGoals.contains("uso su strada"))
        assertFalse(pilotZero.outcomes.any { it.contains("background", ignoreCase = true) })
    }

    @Test
    fun `demo snapshot is built from canonical shared contracts`() {
        val snapshot = PilotCatalog.demoSnapshot()
        assertEquals(4L, snapshot.sampleSequence)
        assertEquals(3_000L, snapshot.monotonicMillis)
        assertEquals(3, snapshot.geometryIndex)
        assertEquals(0.25, snapshot.fractionToNext, 0.0)
        assertEquals(3, snapshot.requiredSuspiciousCount)
        assertEquals(2_000L, snapshot.minimumSuspiciousMillis)
    }
}
