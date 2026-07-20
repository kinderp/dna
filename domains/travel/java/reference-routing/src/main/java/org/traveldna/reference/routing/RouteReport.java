package org.traveldna.reference.routing;

import java.util.StringJoiner;

/** Stable, fixture-scoped one-line report compared across Java and Rust. */
public final class RouteReport {
    private RouteReport() {
    }

    public static String canonicalLine(
            ReferenceScenario scenario, ReferenceRouter router, RouteResult result) {
        final StringJoiner path = new StringJoiner("\",\"", "[\"", "\"]");
        result.path().forEach(path::add);
        return "{\"scenario\":\"" + scenario.id()
                + "\",\"algorithm\":\"" + router.algorithmId()
                + "\",\"origin\":\"" + scenario.origin()
                + "\",\"destination\":\"" + scenario.destination()
                + "\",\"path\":" + path
                + ",\"total_cost_m\":" + result.totalCostMetres()
                + "}";
    }
}
