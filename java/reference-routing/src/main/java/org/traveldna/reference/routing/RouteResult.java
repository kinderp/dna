package org.traveldna.reference.routing;

import java.util.List;

/** Successful deterministic reference route. */
public record RouteResult(List<String> path, long totalCostMetres) {
    public RouteResult {
        path = List.copyOf(path);
        if (path.size() < 2 || totalCostMetres <= 0) {
            throw new IllegalArgumentException("a successful route needs at least two nodes and positive cost");
        }
    }
}
