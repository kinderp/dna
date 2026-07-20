package org.traveldna.reference.routing;

/** Computes one deterministic route in the fixture-scoped road graph. */
public interface ReferenceRouter {
    RouteResult route(RoadGraph graph, String origin, String destination);

    String algorithmId();
}
