use std::cmp::Ordering;
use std::collections::{BTreeMap, BinaryHeap};

use crate::{ReferenceScenario, RoadGraph};

/// Successful deterministic route.
#[derive(Clone, Debug, PartialEq, Eq)]
pub struct RouteResult {
    pub path: Vec<String>,
    pub total_cost_metres: u64,
}

/// Reference algorithm selector shared by the CLI and tests.
#[derive(Clone, Copy, Debug, PartialEq, Eq)]
pub enum Algorithm {
    Dijkstra,
    AStar,
}

impl Algorithm {
    /// Parses the stable CLI identifier.
    ///
    /// # Errors
    ///
    /// Returns an error for identifiers other than `dijkstra` or `astar`.
    pub fn parse(value: &str) -> Result<Self, String> {
        match value {
            "dijkstra" => Ok(Self::Dijkstra),
            "astar" => Ok(Self::AStar),
            _ => Err(format!("unknown algorithm: {value}")),
        }
    }

    /// Returns the stable report identifier.
    #[must_use]
    pub const fn id(self) -> &'static str {
        match self {
            Self::Dijkstra => "dijkstra",
            Self::AStar => "astar",
        }
    }
}

#[derive(Clone, Debug, Eq, PartialEq)]
struct FrontierEntry {
    node: String,
    route_cost: u64,
    estimated_total: u64,
}

impl Ord for FrontierEntry {
    fn cmp(&self, other: &Self) -> Ordering {
        // BinaryHeap is a max-heap. Reverse every comparison for deterministic
        // minimum-first behavior and use node ID as the final tie breaker.
        other
            .estimated_total
            .cmp(&self.estimated_total)
            .then_with(|| other.route_cost.cmp(&self.route_cost))
            .then_with(|| other.node.cmp(&self.node))
    }
}

impl PartialOrd for FrontierEntry {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

/// Computes a deterministic Dijkstra or A* route.
///
/// # Errors
///
/// Returns an error for missing endpoints, integer overflow or disconnected
/// graphs. The function performs no I/O and mutates no graph state.
pub fn route(
    graph: &RoadGraph,
    origin: &str,
    destination: &str,
    algorithm: Algorithm,
) -> Result<RouteResult, String> {
    graph.require_node(origin)?;
    graph.require_node(destination)?;
    if origin == destination {
        return Err("origin and destination must differ in the v0 Lab".to_owned());
    }

    let mut frontier = BinaryHeap::new();
    let mut best_cost: BTreeMap<String, u64> = BTreeMap::new();
    let mut previous: BTreeMap<String, String> = BTreeMap::new();
    best_cost.insert(origin.to_owned(), 0);
    frontier.push(FrontierEntry {
        node: origin.to_owned(),
        route_cost: 0,
        estimated_total: heuristic_metres(graph, origin, destination, algorithm)?,
    });

    while let Some(current) = frontier.pop() {
        let known = best_cost.get(&current.node).copied().unwrap_or(u64::MAX);
        if current.route_cost != known {
            continue;
        }
        if current.node == destination {
            return Ok(RouteResult {
                path: reconstruct_path(&previous, origin, destination)?,
                total_cost_metres: current.route_cost,
            });
        }
        for edge in graph.outgoing(&current.node)? {
            let candidate = current
                .route_cost
                .checked_add(edge.cost_metres)
                .ok_or_else(|| "route cost overflow".to_owned())?;
            let old_cost = best_cost.get(&edge.to).copied().unwrap_or(u64::MAX);
            if candidate < old_cost {
                best_cost.insert(edge.to.clone(), candidate);
                previous.insert(edge.to.clone(), current.node.clone());
                let estimated_total = candidate
                    .checked_add(heuristic_metres(graph, &edge.to, destination, algorithm)?)
                    .ok_or_else(|| "route estimate overflow".to_owned())?;
                frontier.push(FrontierEntry {
                    node: edge.to.clone(),
                    route_cost: candidate,
                    estimated_total,
                });
            }
        }
    }
    Err(format!("no route from {origin} to {destination}"))
}

/// Emits the fixture-scoped report compared byte-for-byte with Java.
#[must_use]
pub fn canonical_report(
    scenario: &ReferenceScenario,
    algorithm: Algorithm,
    result: &RouteResult,
) -> String {
    let path = result
        .path
        .iter()
        .map(|node| format!("\"{node}\""))
        .collect::<Vec<_>>()
        .join(",");
    format!(
        "{{\"scenario\":\"{}\",\"algorithm\":\"{}\",\"origin\":\"{}\",\"destination\":\"{}\",\"path\":[{}],\"total_cost_m\":{}}}",
        scenario.id,
        algorithm.id(),
        scenario.origin,
        scenario.destination,
        path,
        result.total_cost_metres
    )
}

fn heuristic_metres(
    graph: &RoadGraph,
    node: &str,
    destination: &str,
    algorithm: Algorithm,
) -> Result<u64, String> {
    match algorithm {
        Algorithm::Dijkstra => Ok(0),
        Algorithm::AStar => Ok(graph
            .require_node(node)?
            .point
            .distance_metres_to(graph.require_node(destination)?.point)),
    }
}

fn reconstruct_path(
    previous: &BTreeMap<String, String>,
    origin: &str,
    destination: &str,
) -> Result<Vec<String>, String> {
    let mut reversed = vec![destination.to_owned()];
    let mut current = destination;
    while current != origin {
        current = previous
            .get(current)
            .map(String::as_str)
            .ok_or_else(|| "route predecessor chain is incomplete".to_owned())?;
        reversed.push(current.to_owned());
    }
    reversed.reverse();
    Ok(reversed)
}
