package org.traveldna.reference.routing;

/** Dijkstra reference: best-first search with a zero heuristic. */
public final class DijkstraRouter extends AbstractBestFirstRouter {
    @Override
    protected long heuristicMetres(RoadGraph graph, String node, String destination) {
        return 0L;
    }

    @Override
    public String algorithmId() {
        return "dijkstra";
    }
}
