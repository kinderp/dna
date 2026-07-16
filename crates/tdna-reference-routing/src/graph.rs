use std::collections::BTreeMap;

use crate::GeoPoint;

/// A named graph vertex.
#[derive(Clone, Debug, PartialEq)]
pub struct RoadNode {
    pub(crate) id: String,
    pub(crate) point: GeoPoint,
}

/// Directed adjacency entry produced from one bidirectional fixture road.
#[derive(Clone, Debug, PartialEq, Eq)]
pub struct RoadEdge {
    pub(crate) to: String,
    pub(crate) cost_metres: u64,
}

/// Immutable-after-loading graph used by the Rust reference implementation.
#[derive(Clone, Debug, Default)]
pub struct RoadGraph {
    nodes: BTreeMap<String, RoadNode>,
    adjacency: BTreeMap<String, Vec<RoadEdge>>,
}

impl RoadGraph {
    /// Adds one unique node while the fixture is being loaded.
    ///
    /// # Errors
    ///
    /// Returns an error for an invalid or duplicate identifier.
    pub fn add_node(&mut self, id: &str, point: GeoPoint) -> Result<(), String> {
        validate_id(id, "node")?;
        if self.nodes.contains_key(id) {
            return Err(format!("duplicate node: {id}"));
        }
        self.nodes.insert(
            id.to_owned(),
            RoadNode {
                id: id.to_owned(),
                point,
            },
        );
        self.adjacency.insert(id.to_owned(), Vec::new());
        Ok(())
    }

    /// Adds a bidirectional road whose cost is no shorter than straight-line distance.
    ///
    /// # Errors
    ///
    /// Returns an error for unknown endpoints, duplicate roads, self roads,
    /// zero cost or a cost that would invalidate the A* heuristic.
    pub fn add_bidirectional_road(
        &mut self,
        first: &str,
        second: &str,
        cost_metres: u64,
    ) -> Result<(), String> {
        if first == second {
            return Err("self roads are not supported by the v0 fixture".to_owned());
        }
        if cost_metres == 0 {
            return Err("road cost must be positive".to_owned());
        }
        let direct_distance = self
            .require_node(first)?
            .point
            .distance_metres_to(self.require_node(second)?.point);
        if cost_metres < direct_distance {
            return Err(format!(
                "road cost must not be shorter than the WGS84 straight-line distance: {first} <-> {second}"
            ));
        }
        self.add_directed_road(first, second, cost_metres)?;
        self.add_directed_road(second, first, cost_metres)
    }

    fn add_directed_road(&mut self, from: &str, to: &str, cost_metres: u64) -> Result<(), String> {
        let outgoing = self
            .adjacency
            .get_mut(from)
            .ok_or_else(|| format!("unknown node: {from}"))?;
        if outgoing.iter().any(|edge| edge.to == to) {
            return Err(format!("duplicate road: {from} -> {to}"));
        }
        outgoing.push(RoadEdge {
            to: to.to_owned(),
            cost_metres,
        });
        outgoing.sort_by(|left, right| left.to.cmp(&right.to));
        Ok(())
    }

    pub(crate) fn require_node(&self, id: &str) -> Result<&RoadNode, String> {
        self.nodes
            .get(id)
            .ok_or_else(|| format!("unknown node: {id}"))
    }

    pub(crate) fn outgoing(&self, id: &str) -> Result<&[RoadEdge], String> {
        self.require_node(id)?;
        self.adjacency
            .get(id)
            .map(Vec::as_slice)
            .ok_or_else(|| format!("missing adjacency list: {id}"))
    }

    pub(crate) fn path_cost_metres(&self, path: &[String]) -> Result<u64, String> {
        if path.len() < 2 {
            return Err("a path must contain at least two nodes".to_owned());
        }
        let mut total = 0_u64;
        for pair in path.windows(2) {
            let from = &pair[0];
            let to = &pair[1];
            self.require_node(from)?;
            self.require_node(to)?;
            let edge = self
                .outgoing(from)?
                .iter()
                .find(|candidate| candidate.to.as_str() == to.as_str())
                .ok_or_else(|| format!("expected path uses a missing road: {from} -> {to}"))?;
            total = total
                .checked_add(edge.cost_metres)
                .ok_or_else(|| "expected path cost overflow".to_owned())?;
        }
        Ok(total)
    }
}

pub(crate) fn validate_id(value: &str, field: &str) -> Result<(), String> {
    if value.is_empty()
        || !value
            .bytes()
            .all(|byte| byte.is_ascii_alphanumeric() || b"._-".contains(&byte))
    {
        return Err(format!("{field} must match [A-Za-z0-9._-]+"));
    }
    Ok(())
}
