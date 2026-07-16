package org.traveldna.reference.routing;

import java.util.Objects;

/** Directed adjacency entry produced from one bidirectional fixture road. */
public record RoadEdge(String from, String to, long costMetres) {
    public RoadEdge {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        if (from.equals(to)) {
            throw new IllegalArgumentException("self roads are not supported by the v0 fixture");
        }
        if (costMetres <= 0) {
            throw new IllegalArgumentException("road cost must be positive");
        }
    }
}
