//! Deterministic routing algorithms for the first Travel DNA Lab.
//!
//! This crate owns a fixture-scoped graph, strict parser, Dijkstra and A*
//! implementations. It performs no network, database, map-rendering or mobile
//! platform work. The duplicated Java/Rust model is educational and must not be
//! treated as the future production canonical API.

mod fixture;
mod geo;
mod graph;
mod router;

pub use fixture::{ReferenceScenario, parse_fixture};
pub use geo::GeoPoint;
pub use graph::{RoadEdge, RoadGraph, RoadNode};
pub use router::{Algorithm, RouteResult, canonical_report, route};

#[cfg(test)]
mod tests;
