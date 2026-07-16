package org.traveldna.reference.routing;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Dependency-free test suite executed by {@code sh tools/tdna check-java}. */
public final class ReferenceRoutingTestSuite {
    private ReferenceRoutingTestSuite() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("test suite expects the fixture path");
        }
        final Path fixture = Path.of(args[0]);
        testGeoValidation();
        testFixtureAndAlgorithms(fixture);
        testNoRoute();
        testMalformedFixture();
        testInvalidExpectation();
        testInconsistentExpectedCost();
        System.out.println("PASS java reference-routing: 6 scenarios");
    }

    private static void testGeoValidation() {
        expectThrows(IllegalArgumentException.class, () -> new GeoPoint(91.0, 0.0));
        expectThrows(IllegalArgumentException.class, () -> new GeoPoint(0.0, 181.0));
        final long distance = new GeoPoint(0.0, 0.0).distanceMetresTo(new GeoPoint(0.0, 0.01));
        check(distance >= 1_110 && distance <= 1_113, "unexpected haversine distance: " + distance);
        final long antipodal = new GeoPoint(0.0, 0.0).distanceMetresTo(new GeoPoint(0.0, 180.0));
        check(antipodal >= 20_015_000 && antipodal <= 20_016_000,
                "unexpected antipodal distance: " + antipodal);
    }

    private static void testFixtureAndAlgorithms(Path fixture) throws Exception {
        final ReferenceScenario scenario = ReferenceFixtureParser.parse(fixture);
        check(scenario.id().equals("reference-network-v0"), "scenario id");
        check(scenario.expectedCostMetres() == 3_600L, "expected cost");
        check(scenario.expectedPath().equals(List.of("A", "C", "D", "E")), "expected path");

        for (ReferenceRouter router : List.of(new DijkstraRouter(), new AStarRouter())) {
            final RouteResult result = router.route(
                    scenario.graph(), scenario.origin(), scenario.destination());
            check(result.totalCostMetres() == scenario.expectedCostMetres(), router.algorithmId() + " cost");
            check(result.path().equals(scenario.expectedPath()), router.algorithmId() + " path");
            final String report = RouteReport.canonicalLine(scenario, router, result);
            check(report.contains("\"algorithm\":\"" + router.algorithmId() + "\""), "report algorithm");
        }
    }

    private static void testNoRoute() {
        final RoadGraph graph = new RoadGraph();
        graph.addNode(new RoadNode("A", new GeoPoint(0.0, 0.0)));
        graph.addNode(new RoadNode("B", new GeoPoint(0.0, 0.01)));
        expectThrows(IllegalStateException.class, () -> new DijkstraRouter().route(graph, "A", "B"));
    }

    private static void testMalformedFixture() throws Exception {
        final Path malformed = Files.createTempFile("tdna-reference-routing", ".tdna");
        try {
            Files.writeString(malformed, "TDNA_REFERENCE_GRAPH_V0\nscenario broken\nunknown x\n");
            expectThrows(IllegalArgumentException.class, () -> ReferenceFixtureParser.parse(malformed));
        } finally {
            Files.deleteIfExists(malformed);
        }
    }

    private static void testInvalidExpectation() throws Exception {
        final Path malformed = Files.createTempFile("tdna-reference-routing", ".tdna");
        try {
            Files.writeString(malformed, String.join("\n",
                    "TDNA_REFERENCE_GRAPH_V0",
                    "scenario broken-expectation",
                    "node A 0.0 0.0",
                    "node B 0.0 0.01",
                    "road A B 1200",
                    "query A B",
                    "expect 1200 B,A",
                    ""));
            expectThrows(IllegalArgumentException.class, () -> ReferenceFixtureParser.parse(malformed));
        } finally {
            Files.deleteIfExists(malformed);
        }
    }

    private static void testInconsistentExpectedCost() throws Exception {
        final Path malformed = Files.createTempFile("tdna-reference-routing", ".tdna");
        try {
            Files.writeString(malformed, String.join("\n",
                    "TDNA_REFERENCE_GRAPH_V0",
                    "scenario inconsistent-cost",
                    "node A 0.0 0.0",
                    "node B 0.0 0.01",
                    "road A B 1200",
                    "query A B",
                    "expect 1300 A,B",
                    ""));
            expectThrows(IllegalArgumentException.class, () -> ReferenceFixtureParser.parse(malformed));
        } finally {
            Files.deleteIfExists(malformed);
        }
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError("failed: " + description);
        }
    }

    private static void expectThrows(Class<? extends Throwable> type, ThrowingAction action) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (type.isInstance(throwable)) {
                return;
            }
            throw new AssertionError("expected " + type.getSimpleName() + " but got " + throwable, throwable);
        }
        throw new AssertionError("expected " + type.getSimpleName() + " but nothing was thrown");
    }

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws Exception;
    }
}
