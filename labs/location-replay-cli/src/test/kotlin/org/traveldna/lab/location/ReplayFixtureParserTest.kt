package org.traveldna.lab.location

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.traveldna.location.replay.DeterministicReplayRunner

class ReplayFixtureParserTest {
    @Test
    fun parsesReferenceFixtureAndMatchesDeterministicSummary() {
        val fixture = ReplayFixtureParser.parse(referenceFixture())
        val summary = DeterministicReplayRunner(fixture.scenario).runToEnd()
        fixture.expectations.requireMatches(summary)
        assertEquals(
            "{\"scenario\":\"reference-location-replay-v0\"," +
                "\"rate\":\"2/1\",\"state\":\"completed\"," +
                "\"processed\":6,\"accepted\":4,\"rejected\":2," +
                "\"rejection_counts\":{" +
                "\"non_increasing_monotonic_time\":1," +
                "\"non_increasing_sequence\":1}," +
                "\"final_time_ms\":3000,\"playback_delay_ms\":1500," +
                "\"last_sequence\":4}",
            canonicalReplayReport(fixture.scenario, summary),
        )
    }

    @Test
    fun rejectsUnknownOriginAndSamplesAfterExpectations() {
        withFixture(
            """
            TDNA_LOCATION_REPLAY_V0
            scenario invalid-origin-v0
            rate 1 1
            sample 0 0 0.0 0.0 5.0 - - platform
            expect accepted 1
            expect rejected 0
            expect final_time_ms 0
            expect playback_delay_ms 0
            expect last_sequence 0
            """.trimIndent(),
        ) { path ->
            assertFailsWith<IllegalArgumentException> { ReplayFixtureParser.parse(path) }
        }

        withFixture(
            """
            TDNA_LOCATION_REPLAY_V0
            scenario late-sample-v0
            rate 1 1
            sample 0 0 0.0 0.0 5.0 - - replay
            expect accepted 1
            sample 1 1000 0.0 0.01 5.0 - - replay
            expect rejected 0
            expect final_time_ms 0
            expect playback_delay_ms 0
            expect last_sequence 0
            """.trimIndent(),
        ) { path ->
            assertFailsWith<IllegalArgumentException> { ReplayFixtureParser.parse(path) }
        }
    }

    @Test
    fun rejectsDuplicateAndInconsistentExpectations() {
        withFixture(
            """
            TDNA_LOCATION_REPLAY_V0
            scenario duplicate-expect-v0
            rate 1 1
            sample 0 0 0.0 0.0 5.0 - - replay
            expect accepted 1
            expect accepted 1
            expect rejected 0
            expect final_time_ms 0
            expect playback_delay_ms 0
            expect last_sequence 0
            """.trimIndent(),
        ) { path ->
            assertFailsWith<IllegalArgumentException> { ReplayFixtureParser.parse(path) }
        }

        withFixture(
            """
            TDNA_LOCATION_REPLAY_V0
            scenario inconsistent-count-v0
            rate 1 1
            sample 0 0 0.0 0.0 5.0 - - replay
            expect accepted 2
            expect rejected 0
            expect final_time_ms 0
            expect playback_delay_ms 0
            expect last_sequence 0
            """.trimIndent(),
        ) { path ->
            assertFailsWith<IllegalArgumentException> { ReplayFixtureParser.parse(path) }
        }
    }

    @Test
    fun rejectsMissingHeaderAndOversizedEmptyContract() {
        withFixture("scenario missing-header-v0") { path ->
            assertFailsWith<IllegalArgumentException> { ReplayFixtureParser.parse(path) }
        }
        val empty = createTempFile("tdna-location-empty", ".tdna")
        try {
            Files.writeString(empty, "")
            assertFailsWith<IllegalArgumentException> { ReplayFixtureParser.parse(empty) }
        } finally {
            Files.deleteIfExists(empty)
        }
    }

    private fun referenceFixture(): Path = Path.of(
        "fixtures/gps/reference-location-replay-v0.tdna",
    ).toAbsolutePath()

    private fun withFixture(text: String, block: (Path) -> Unit) {
        val path = createTempFile("tdna-location-replay", ".tdna")
        try {
            Files.writeString(path, text)
            block(path)
        } finally {
            Files.deleteIfExists(path)
        }
    }
}
