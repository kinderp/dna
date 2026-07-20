package org.traveldna.reference.routing;

/** A* reference using the floored WGS84 straight-line distance as heuristic. */
public final class AStarRouter extends AbstractBestFirstRouter {
    @Override
    protected long heuristicMetres(RoadGraph graph, String node, String destination) {
        return graph.requireNode(node).point()
                .distanceMetresTo(graph.requireNode(destination).point());
    }

    @Override
    public String algorithmId() {
        return "astar";
    }
}
