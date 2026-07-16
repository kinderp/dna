package org.traveldna.reference.routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable-after-loading graph used by the Java routing reference.
 *
 * <p>The fixture loader is the only intended mutator. Routing algorithms read
 * the graph without performing I/O or changing graph state.</p>
 */
public final class RoadGraph {
    private final Map<String, RoadNode> nodes = new LinkedHashMap<>();
    private final Map<String, List<RoadEdge>> adjacency = new LinkedHashMap<>();

    public void addNode(RoadNode node) {
        Objects.requireNonNull(node, "node");
        if (nodes.putIfAbsent(node.id(), node) != null) {
            throw new IllegalArgumentException("duplicate node: " + node.id());
        }
        adjacency.put(node.id(), new ArrayList<>());
    }

    public void addBidirectionalRoad(String first, String second, long costMetres) {
        final RoadNode firstNode = requireNode(first);
        final RoadNode secondNode = requireNode(second);
        final long directDistance = firstNode.point().distanceMetresTo(secondNode.point());
        if (costMetres < directDistance) {
            throw new IllegalArgumentException(
                    "road cost must not be shorter than the WGS84 straight-line distance: "
                            + first + " <-> " + second);
        }
        addDirectedRoad(first, second, costMetres);
        addDirectedRoad(second, first, costMetres);
    }

    private void addDirectedRoad(String from, String to, long costMetres) {
        final List<RoadEdge> outgoing = adjacency.get(from);
        for (RoadEdge existing : outgoing) {
            if (existing.to().equals(to)) {
                throw new IllegalArgumentException("duplicate road: " + from + " -> " + to);
            }
        }
        outgoing.add(new RoadEdge(from, to, costMetres));
        outgoing.sort((left, right) -> left.to().compareTo(right.to()));
    }

    public RoadNode requireNode(String id) {
        final RoadNode node = nodes.get(id);
        if (node == null) {
            throw new IllegalArgumentException("unknown node: " + id);
        }
        return node;
    }

    public List<RoadEdge> outgoing(String id) {
        requireNode(id);
        return Collections.unmodifiableList(adjacency.get(id));
    }

    public Collection<RoadNode> nodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }
}
