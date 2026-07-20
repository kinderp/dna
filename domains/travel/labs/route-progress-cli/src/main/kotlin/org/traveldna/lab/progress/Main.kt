package org.traveldna.lab.progress

import java.util.Locale
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.map.contracts.MapItemId
import org.traveldna.map.contracts.MapSceneId
import org.traveldna.map.contracts.RouteOverlay
import org.traveldna.map.contracts.RouteOverlayRole
import org.traveldna.navigation.contracts.MatchConfidence
import org.traveldna.navigation.contracts.MatchedRoutePosition
import org.traveldna.navigation.contracts.RouteCoordinate
import org.traveldna.navigation.contracts.RouteProgressDecision
import org.traveldna.navigation.map.RouteProgressMapProjector
import org.traveldna.navigation.progress.RouteProgressTracker
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.ManeuverType
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RouteManeuver
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RouteProvenance

fun main(args: Array<String>) {
    when {
        args.isEmpty() -> runLab()
        args.size in 2..3 && args[0] == "--benchmark" -> {
            val samples = args[1].toInt()
            val iterations = args.getOrNull(2)?.toInt() ?: DefaultBenchmarkIterations
            runBenchmark(samples, iterations)
        }
        else -> {
            System.err.println("usage: route-progress-cli | --benchmark SAMPLE_COUNT [ODD_ITERATIONS]")
            kotlin.system.exitProcess(2)
        }
    }
}

private fun runLab() {
    val route = referenceRoute()
    val tracker = RouteProgressTracker(route)
    val positions = listOf(
        position(route.id, 0, 0, 0, 0.0),
        position(route.id, 1, 1_000, 0, 0.5),
        position(route.id, 2, 2_000, 2, 0.0),
        position(route.id, 3, 3_000, 1, 0.75),
        position(route.id, 4, 4_000, 2, 0.5),
        position(route.id, 5, 5_000, 3, 0.0),
    )

    var accepted = 0
    var rejected = 0
    var rejectionReason = "none"
    var boundaryLeg = -1
    var boundaryManeuver = "none"
    positions.forEach { candidate ->
        when (val decision = tracker.accept(candidate)) {
            is RouteProgressDecision.Accepted -> {
                accepted += 1
                if (candidate.sampleSequence.value == 2L) {
                    boundaryLeg = decision.snapshot.activeLegIndex
                    boundaryManeuver = decision.snapshot.upcomingManeuver?.maneuver?.type?.name ?: "none"
                }
            }
            is RouteProgressDecision.Rejected -> {
                rejected += 1
                rejectionReason = decision.reason.name
            }
        }
    }

    val finalSnapshot = checkNotNull(tracker.lastSnapshot)
    val overlay = RouteOverlay(
        id = MapItemId("route.reference-progress"),
        routeId = route.id,
        geometry = route.geometry,
        role = RouteOverlayRole.Primary,
    )
    val mapBinding = RouteProgressMapProjector.bind(
        sceneId = MapSceneId("scene.reference-progress-v0"),
        overlay = overlay,
        route = route,
    )
    val delta = RouteProgressMapProjector.project(mapBinding, finalSnapshot)
    val finalManeuver = finalSnapshot.upcomingManeuver?.maneuver?.type?.name ?: "none"

    check(accepted == ExpectedAccepted)
    check(rejected == ExpectedRejected)
    check(rejectionReason == "RegressedAlongRoute")
    check(boundaryLeg == 1)
    check(boundaryManeuver == ManeuverType.Continue.name)
    check(finalSnapshot.position.coordinate == RouteCoordinate(3, 0.0))
    check(finalSnapshot.activeLegIndex == 1)
    check(finalManeuver == ManeuverType.Arrive.name)
    check(finalSnapshot.arrived)
    check(delta.progress.completedGeometryIndex == 3)
    check(delta.progress.fractionToNext == 0.0)

    println(
        "{\"scenario\":\"reference-route-progress-v0\"," +
            "\"accepted\":$accepted," +
            "\"rejected\":$rejected," +
            "\"rejection\":\"$rejectionReason\"," +
            "\"boundary_leg\":$boundaryLeg," +
            "\"boundary_maneuver\":\"$boundaryManeuver\"," +
            "\"completed_index\":${finalSnapshot.position.coordinate.completedGeometryIndex}," +
            "\"fraction\":${finalSnapshot.position.coordinate.fractionToNext}," +
            "\"active_leg\":${finalSnapshot.activeLegIndex}," +
            "\"upcoming_maneuver\":\"$finalManeuver\"," +
            "\"arrived\":${finalSnapshot.arrived}," +
            "\"delta_index\":${delta.progress.completedGeometryIndex}}",
    )
}

private fun runBenchmark(sampleCount: Int, iterations: Int) {
    require(sampleCount in 2..MaxBenchmarkSamples) {
        "benchmark sample count must be within [2, $MaxBenchmarkSamples]"
    }
    require(iterations in 1..MaxBenchmarkIterations && iterations % 2 == 1) {
        "benchmark iterations must be odd and within [1, $MaxBenchmarkIterations]"
    }
    val route = benchmarkRoute(sampleCount)
    val positions = List(sampleCount) { index ->
        position(route.id, index.toLong(), index.toLong() * 100L, index, 0.0)
    }
    val tracker = RouteProgressTracker(route)

    repeat(BenchmarkWarmups) {
        tracker.reset()
        runBenchmarkIteration(tracker, positions)
        verifyBenchmarkOutcome(tracker, sampleCount)
    }
    val elapsed = LongArray(iterations) {
        tracker.reset()
        val started = System.nanoTime()
        runBenchmarkIteration(tracker, positions)
        val duration = System.nanoTime() - started
        verifyBenchmarkOutcome(tracker, sampleCount)
        duration
    }.sorted()
    val median = elapsed[elapsed.size / 2]
    val maneuverCount = route.legs.sumOf { it.maneuvers.size }
    println(
        "{\"benchmark\":\"route-progress-v0\"," +
            "\"samples\":$sampleCount," +
            "\"legs\":${route.legs.size}," +
            "\"maneuvers\":$maneuverCount," +
            "\"warmups\":$BenchmarkWarmups," +
            "\"iterations\":$iterations," +
            "\"min_elapsed_ns\":${elapsed.first()}," +
            "\"median_elapsed_ns\":$median," +
            "\"max_elapsed_ns\":${elapsed.last()}," +
            "\"median_ns_per_sample\":${"%.2f".format(Locale.ROOT, median.toDouble() / sampleCount)}}",
    )
}

private fun runBenchmarkIteration(
    tracker: RouteProgressTracker,
    positions: List<MatchedRoutePosition>,
) {
    positions.forEach(tracker::accept)
}

private fun verifyBenchmarkOutcome(tracker: RouteProgressTracker, sampleCount: Int) {
    val finalSnapshot = checkNotNull(tracker.lastSnapshot)
    check(finalSnapshot.arrived)
    check(finalSnapshot.position.sampleSequence == LocationSequence((sampleCount - 1).toLong()))
    check(finalSnapshot.position.coordinate == RouteCoordinate(sampleCount - 1, 0.0))
}

private fun referenceRoute(): RoutePlan {
    val points = listOf(
        GeoPoint(0.0, 0.0),
        GeoPoint(0.0, 0.01),
        GeoPoint(0.01, 0.01),
        GeoPoint(0.01, 0.02),
    )
    return RoutePlan(
        id = RouteId("reference-progress-route-v0"),
        geometry = points,
        legs = listOf(
            RouteLeg(
                0,
                2,
                points[0],
                points[2],
                2_400L,
                160L,
                listOf(
                    RouteManeuver(0, ManeuverType.Depart, points[0], "Depart"),
                    RouteManeuver(1, ManeuverType.TurnRight, points[1], "Turn right"),
                    RouteManeuver(2, ManeuverType.KeepRight, points[2], "Finish first leg"),
                ),
            ),
            RouteLeg(
                2,
                3,
                points[2],
                points[3],
                1_200L,
                80L,
                listOf(
                    RouteManeuver(2, ManeuverType.Continue, points[2], "Continue second leg"),
                    RouteManeuver(3, ManeuverType.Arrive, points[3], "Arrive"),
                ),
            ),
        ),
        distanceMeters = 3_600L,
        durationSeconds = 240L,
        provenance = RouteProvenance(PluginId("org.traveldna.progress-lab")),
    )
}

private fun benchmarkRoute(sampleCount: Int): RoutePlan {
    val points = List(sampleCount) { index -> GeoPoint(0.0, index * 0.000001) }
    val segmentCount = points.lastIndex
    val legCount = minOf(MaxBenchmarkLegs, segmentCount)
    val legs = List(legCount) { legIndex ->
        val start = legIndex * segmentCount / legCount
        val end = (legIndex + 1) * segmentCount / legCount
        check(end > start)
        val maneuvers = buildList {
            add(
                RouteManeuver(
                    geometryIndex = start,
                    type = if (legIndex == 0) ManeuverType.Depart else ManeuverType.Continue,
                    location = points[start],
                    instruction = if (legIndex == 0) "Depart" else "Continue leg $legIndex",
                ),
            )
            if (legIndex == legCount - 1) {
                add(RouteManeuver(end, ManeuverType.Arrive, points[end], "Arrive"))
            }
        }
        val distance = (end - start).toLong()
        RouteLeg(
            geometryStartIndex = start,
            geometryEndIndex = end,
            origin = points[start],
            destination = points[end],
            distanceMeters = distance,
            durationSeconds = distance,
            maneuvers = maneuvers,
        )
    }
    val total = segmentCount.toLong()
    return RoutePlan(
        id = RouteId("benchmark-progress-route-v0"),
        geometry = points,
        legs = legs,
        distanceMeters = total,
        durationSeconds = total,
        provenance = RouteProvenance(PluginId("org.traveldna.progress-benchmark")),
    )
}

private fun position(
    routeId: RouteId,
    sequence: Long,
    time: Long,
    index: Int,
    fraction: Double,
): MatchedRoutePosition = MatchedRoutePosition(
    routeId = routeId,
    sampleSequence = LocationSequence(sequence),
    monotonicTime = MonotonicInstant(time),
    coordinate = RouteCoordinate(index, fraction),
    lateralDistanceMeters = 1.5,
    confidence = MatchConfidence.High,
)

private const val ExpectedAccepted: Int = 5
private const val ExpectedRejected: Int = 1
private const val BenchmarkWarmups: Int = 3
private const val DefaultBenchmarkIterations: Int = 7
private const val MaxBenchmarkIterations: Int = 25
private const val MaxBenchmarkSamples: Int = 100_000
private const val MaxBenchmarkLegs: Int = 100
