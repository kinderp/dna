package org.traveldna.reference.routing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses the deliberately small {@code TDNA_REFERENCE_GRAPH_V0} fixture format.
 *
 * <p>The parser is strict: unknown directives, duplicate declarations and
 * trailing tokens are rejected so teaching fixtures cannot silently drift.</p>
 */
public final class ReferenceFixtureParser {
    private ReferenceFixtureParser() {
    }

    public static ReferenceScenario parse(Path path) throws IOException {
        final List<String> lines = Files.readAllLines(path);
        final RoadGraph graph = new RoadGraph();
        String scenarioId = null;
        String origin = null;
        String destination = null;
        Long expectedCost = null;
        List<String> expectedPath = null;
        boolean versionSeen = false;

        for (int index = 0; index < lines.size(); index++) {
            final String raw = lines.get(index);
            final String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            final String[] parts = line.split("\\s+");
            try {
                switch (parts[0]) {
                    case "TDNA_REFERENCE_GRAPH_V0" -> {
                        requireArity(parts, 1);
                        if (versionSeen) {
                            throw new IllegalArgumentException("duplicate version header");
                        }
                        versionSeen = true;
                    }
                    case "scenario" -> {
                        requireArity(parts, 2);
                        if (scenarioId != null) {
                            throw new IllegalArgumentException("duplicate scenario declaration");
                        }
                        scenarioId = requireId(parts[1], "scenario");
                    }
                    case "node" -> {
                        requireArity(parts, 4);
                        ensureVersion(versionSeen);
                        graph.addNode(new RoadNode(
                                requireId(parts[1], "node"),
                                new GeoPoint(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]))));
                    }
                    case "road" -> {
                        requireArity(parts, 4);
                        ensureVersion(versionSeen);
                        graph.addBidirectionalRoad(
                                requireId(parts[1], "road endpoint"),
                                requireId(parts[2], "road endpoint"),
                                Long.parseLong(parts[3]));
                    }
                    case "query" -> {
                        requireArity(parts, 3);
                        if (origin != null) {
                            throw new IllegalArgumentException("duplicate query declaration");
                        }
                        origin = requireId(parts[1], "origin");
                        destination = requireId(parts[2], "destination");
                    }
                    case "expect" -> {
                        requireArity(parts, 3);
                        if (expectedCost != null) {
                            throw new IllegalArgumentException("duplicate expectation declaration");
                        }
                        expectedCost = Long.parseLong(parts[1]);
                        expectedPath = parsePath(parts[2]);
                    }
                    default -> throw new IllegalArgumentException("unknown directive: " + parts[0]);
                }
            } catch (RuntimeException exception) {
                throw new IllegalArgumentException(
                        path + ":" + (index + 1) + ": " + exception.getMessage(), exception);
            }
        }

        if (!versionSeen || scenarioId == null || origin == null || destination == null
                || expectedCost == null || expectedPath == null) {
            throw new IllegalArgumentException("fixture is missing a required declaration");
        }
        graph.requireNode(origin);
        graph.requireNode(destination);
        if (expectedCost <= 0 || expectedPath.size() < 2) {
            throw new IllegalArgumentException("fixture expectation must contain a positive route");
        }
        if (!expectedPath.getFirst().equals(origin)
                || !expectedPath.getLast().equals(destination)) {
            throw new IllegalArgumentException(
                    "expected path must start at the query origin and end at the destination");
        }
        expectedPath.forEach(graph::requireNode);
        final long expectedPathCost = graph.pathCostMetres(expectedPath);
        if (expectedPathCost != expectedCost) {
            throw new IllegalArgumentException(
                    "expected cost " + expectedCost
                            + " does not match expected path cost " + expectedPathCost);
        }
        return new ReferenceScenario(
                scenarioId, graph, origin, destination, expectedCost, expectedPath);
    }

    private static List<String> parsePath(String value) {
        final String[] nodes = value.split(",", -1);
        final List<String> path = new ArrayList<>(nodes.length);
        for (String node : nodes) {
            path.add(requireId(node, "expected path node"));
        }
        return path;
    }

    private static void ensureVersion(boolean versionSeen) {
        if (!versionSeen) {
            throw new IllegalArgumentException("version header must appear before graph data");
        }
    }

    private static String requireId(String value, String field) {
        if (!value.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalArgumentException(field + " must match [A-Za-z0-9._-]+");
        }
        return value;
    }

    private static void requireArity(String[] parts, int expected) {
        if (parts.length != expected) {
            throw new IllegalArgumentException(
                    "directive " + parts[0] + " expects " + (expected - 1) + " argument(s)");
        }
    }
}
