package org.traveldna.reference.routing;

import java.util.Objects;

/** A named vertex in the educational road graph. */
public record RoadNode(String id, GeoPoint point) {
    public RoadNode {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(point, "point");
        if (!id.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalArgumentException("node id must match [A-Za-z0-9._-]+");
        }
    }
}
