package org.traveldna.reference.routing;

import java.util.List;
import java.util.Objects;

/** Parsed fixture plus the expected ground truth used by tests and the Lab CLI. */
public record ReferenceScenario(
        String id,
        RoadGraph graph,
        String origin,
        String destination,
        long expectedCostMetres,
        List<String> expectedPath) {
    public ReferenceScenario {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(graph, "graph");
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(destination, "destination");
        expectedPath = List.copyOf(expectedPath);
        if (expectedCostMetres <= 0 || expectedPath.size() < 2) {
            throw new IllegalArgumentException("fixture expectation must contain a positive route");
        }
        graph.requireNode(origin);
        graph.requireNode(destination);
    }
}
