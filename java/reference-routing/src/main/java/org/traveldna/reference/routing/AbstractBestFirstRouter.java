package org.traveldna.reference.routing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/** Shared deterministic best-first search used by Dijkstra and A*. */
abstract class AbstractBestFirstRouter implements ReferenceRouter {
    private record FrontierEntry(String node, long routeCost, long estimatedTotal) {
    }

    @Override
    public final RouteResult route(RoadGraph graph, String origin, String destination) {
        graph.requireNode(origin);
        graph.requireNode(destination);
        if (origin.equals(destination)) {
            throw new IllegalArgumentException("origin and destination must differ in the v0 Lab");
        }

        final Comparator<FrontierEntry> order = Comparator
                .comparingLong(FrontierEntry::estimatedTotal)
                .thenComparingLong(FrontierEntry::routeCost)
                .thenComparing(FrontierEntry::node);
        final PriorityQueue<FrontierEntry> frontier = new PriorityQueue<>(order);
        final Map<String, Long> bestCost = new HashMap<>();
        final Map<String, String> previous = new HashMap<>();

        bestCost.put(origin, 0L);
        frontier.add(new FrontierEntry(
                origin, 0L, heuristicMetres(graph, origin, destination)));

        while (!frontier.isEmpty()) {
            final FrontierEntry current = frontier.remove();
            if (current.routeCost() != bestCost.getOrDefault(current.node(), Long.MAX_VALUE)) {
                continue;
            }
            if (current.node().equals(destination)) {
                return new RouteResult(
                        reconstructPath(previous, origin, destination), current.routeCost());
            }
            for (RoadEdge edge : graph.outgoing(current.node())) {
                final long candidate = Math.addExact(current.routeCost(), edge.costMetres());
                final long known = bestCost.getOrDefault(edge.to(), Long.MAX_VALUE);
                if (candidate < known) {
                    bestCost.put(edge.to(), candidate);
                    previous.put(edge.to(), current.node());
                    final long estimate = Math.addExact(
                            candidate, heuristicMetres(graph, edge.to(), destination));
                    frontier.add(new FrontierEntry(edge.to(), candidate, estimate));
                }
            }
        }
        throw new IllegalStateException("no route from " + origin + " to " + destination);
    }

    protected abstract long heuristicMetres(RoadGraph graph, String node, String destination);

    private static List<String> reconstructPath(
            Map<String, String> previous, String origin, String destination) {
        final List<String> reversed = new ArrayList<>();
        String current = destination;
        reversed.add(current);
        while (!current.equals(origin)) {
            current = previous.get(current);
            if (current == null) {
                throw new IllegalStateException("route predecessor chain is incomplete");
            }
            reversed.add(current);
        }
        final List<String> path = new ArrayList<>(reversed.size());
        for (int index = reversed.size() - 1; index >= 0; index--) {
            path.add(reversed.get(index));
        }
        return path;
    }
}
