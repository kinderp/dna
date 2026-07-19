package org.traveldna.android.pilot

import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.navigation.contracts.RouteCoordinate
import org.traveldna.navigation.offroute.contracts.OffRoutePolicy

enum class PilotStage {
    TeachingShell,
    RoadCompanion,
    ClosedBeta,
}

data class PilotMilestone(
    val stage: PilotStage,
    val title: String,
    val window: String,
    val audience: String,
    val outcomes: List<String>,
    val nonGoals: List<String>,
)

data class StudyStep(
    val order: Int,
    val title: String,
    val purpose: String,
    val repositoryExercise: String,
)

data class DemoSnapshot(
    val sampleSequence: Long,
    val monotonicMillis: Long,
    val latitude: Double,
    val longitude: Double,
    val geometryIndex: Int,
    val fractionToNext: Double,
    val requiredSuspiciousCount: Int,
    val minimumSuspiciousMillis: Long,
)

object PilotCatalog {
    val milestones: List<PilotMilestone> = listOf(
        PilotMilestone(
            stage = PilotStage.TeachingShell,
            title = "Pilot 0 — shell didattica installabile",
            window = "10–21 agosto 2026",
            audience = "Docente, studenti, emulatore e 1–3 telefoni",
            outcomes = listOf(
                "APK debug identificata da commit e SHA-256",
                "Jetpack Compose e composition root Android",
                "Replay, route progress e missed-exit sintetici",
                "Schermate collegate ai capitoli del progetto",
            ),
            nonGoals = listOf("GPS reale", "MapLibre", "chat", "uso su strada"),
        ),
        PilotMilestone(
            stage = PilotStage.RoadCompanion,
            title = "Pilot 1 — companion stradale controllato",
            window = "21 settembre–9 ottobre 2026",
            audience = "5–10 tester invitati su percorsi dichiarati",
            outcomes = listOf(
                "Posizione foreground trasparente",
                "Sessione viaggio start/pausa/fine",
                "Handoff al navigatore esterno",
                "Recorder locale bounded e diagnostica redatta",
            ),
            nonGoals = listOf(
                "turn-by-turn di produzione",
                "tracking pubblico",
                "Android Auto",
            ),
        ),
        PilotMilestone(
            stage = PilotStage.ClosedBeta,
            title = "Pilot 2 — piccola beta chiusa",
            window = "2 novembre–11 dicembre 2026, da ristimare",
            audience = "15–30 tester dopo evidenze Pilot 1",
            outcomes = listOf(
                "Prima mappa reale candidata",
                "Recupero sessione più robusto",
                "Seed account/backend o conversazione limitata",
                "Matrice dispositivi e batteria più ampia",
            ),
            nonGoals = listOf("lancio pubblico", "SLA stradale", "parità con Waze"),
        ),
    )

    val studySteps: List<StudyStep> = listOf(
        StudyStep(
            1,
            "Kotlin idiomatico",
            "Leggere e modificare value object e sealed interface.",
            "Eseguire i test dei moduli shared.",
        ),
        StudyStep(
            2,
            "Android e lifecycle",
            "Capire Activity, processo, configurazione e stato.",
            "Seguire MainActivity e ricreare la shell dopo rotazione.",
        ),
        StudyStep(
            3,
            "Jetpack Compose",
            "UI dichiarativa, state hoisting ed effetti.",
            "Aggiungere una card senza introdurre stato globale.",
        ),
        StudyStep(
            4,
            "Coroutines e Flow",
            "Cancellazione e structured concurrency.",
            "Rileggere replay e reroute executor.",
        ),
        StudyStep(
            5,
            "Test Android",
            "Separare unit, UI compile-smoke e device test.",
            "Estendere PilotCatalogTest e lo smoke test Compose.",
        ),
        StudyStep(
            6,
            "Permessi e foreground service",
            "Prepararsi al Pilot 1 senza anticiparlo.",
            "Scrivere una threat checklist prima del codice GPS.",
        ),
    )

    val optionalPurchases: List<String> = listOf(
        "Nessun nuovo corso: Manning e Pluralsight coprono il percorso iniziale.",
        "Un telefono Android reale serve prima del Pilot 1 se non ne possiedi uno adatto.",
        "Supporto auto e alimentazione USB servono solo ai field test con osservatore.",
        "Play Console, SDK mappe a pagamento e hardware LoRa non servono al Pilot 0.",
    )

    fun demoSnapshot(): DemoSnapshot {
        val sample = LocationSample(
            sequence = LocationSequence(4),
            monotonicTime = MonotonicInstant(3_000),
            position = GeoPoint(45.4642, 9.1900),
            horizontalAccuracyMeters = 6.0,
            speedMetersPerSecond = 13.5,
            bearingDegrees = 90.0,
            origin = LocationSampleOrigin.Replay,
        )
        val coordinate = RouteCoordinate(completedGeometryIndex = 3, fractionToNext = 0.25)
        val policy = OffRoutePolicy()
        return DemoSnapshot(
            sampleSequence = sample.sequence.value,
            monotonicMillis = sample.monotonicTime.milliseconds,
            latitude = sample.position.latitude,
            longitude = sample.position.longitude,
            geometryIndex = coordinate.completedGeometryIndex,
            fractionToNext = coordinate.fractionToNext,
            requiredSuspiciousCount = policy.requiredConsecutiveSuspicious,
            minimumSuspiciousMillis = policy.minimumSuspiciousDurationMillis,
        )
    }
}
