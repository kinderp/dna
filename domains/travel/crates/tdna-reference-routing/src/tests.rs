use std::fs;
use std::io::Write;
use std::path::Path;

use crate::{Algorithm, GeoPoint, RoadGraph, canonical_report, parse_fixture, route};

const FIXTURE: &str = concat!(
    env!("CARGO_MANIFEST_DIR"),
    "/../../fixtures/routes/reference-network-v0.tdna"
);

#[test]
fn validates_geo_points() {
    assert!(GeoPoint::new(91.0, 0.0).is_err());
    assert!(GeoPoint::new(0.0, 181.0).is_err());
    let distance = GeoPoint::new(0.0, 0.0)
        .expect("point")
        .distance_metres_to(GeoPoint::new(0.0, 0.01).expect("point"));
    assert!((1_110..=1_113).contains(&distance));
    let antipodal = GeoPoint::new(0.0, 0.0)
        .expect("point")
        .distance_metres_to(GeoPoint::new(0.0, 180.0).expect("point"));
    assert!((20_015_000..=20_016_000).contains(&antipodal));
}

#[test]
fn both_algorithms_match_ground_truth() {
    let scenario = parse_fixture(Path::new(FIXTURE)).expect("fixture must parse");
    for algorithm in [Algorithm::Dijkstra, Algorithm::AStar] {
        let result = route(
            &scenario.graph,
            &scenario.origin,
            &scenario.destination,
            algorithm,
        )
        .expect("route must exist");
        assert_eq!(result.total_cost_metres, scenario.expected_cost_metres);
        assert_eq!(result.path, scenario.expected_path);
        assert!(
            canonical_report(&scenario, algorithm, &result)
                .contains(&format!("\"algorithm\":\"{}\"", algorithm.id()))
        );
    }
}

#[test]
fn disconnected_graph_returns_error() {
    let mut graph = RoadGraph::default();
    graph
        .add_node("A", GeoPoint::new(0.0, 0.0).expect("point"))
        .expect("node");
    graph
        .add_node("B", GeoPoint::new(0.0, 0.01).expect("point"))
        .expect("node");
    assert!(route(&graph, "A", "B", Algorithm::Dijkstra).is_err());
}

#[test]
fn malformed_fixture_is_rejected() {
    let path = std::env::temp_dir().join(format!(
        "tdna-reference-routing-{}-malformed.tdna",
        std::process::id()
    ));
    let mut file = fs::File::create(&path).expect("create fixture");
    writeln!(file, "TDNA_REFERENCE_GRAPH_V0\nscenario broken\nunknown x").expect("write fixture");
    let result = parse_fixture(&path);
    fs::remove_file(&path).expect("remove fixture");
    assert!(result.is_err());
}

#[test]
fn invalid_expectation_is_rejected() {
    let path = std::env::temp_dir().join(format!(
        "tdna-reference-routing-{}-invalid-expectation.tdna",
        std::process::id()
    ));
    let mut file = fs::File::create(&path).expect("create fixture");
    writeln!(
        file,
        "TDNA_REFERENCE_GRAPH_V0\nscenario broken-expectation\nnode A 0.0 0.0\nnode B 0.0 0.01\nroad A B 1200\nquery A B\nexpect 1200 B,A"
    )
    .expect("write fixture");
    let result = parse_fixture(&path);
    fs::remove_file(&path).expect("remove fixture");
    assert!(result.is_err());
}

#[test]
fn inconsistent_expected_cost_is_rejected() {
    let path = std::env::temp_dir().join(format!(
        "tdna-reference-routing-{}-inconsistent-cost.tdna",
        std::process::id()
    ));
    let mut file = fs::File::create(&path).expect("create fixture");
    writeln!(
        file,
        "TDNA_REFERENCE_GRAPH_V0\nscenario inconsistent-cost\nnode A 0.0 0.0\nnode B 0.0 0.01\nroad A B 1200\nquery A B\nexpect 1300 A,B"
    )
    .expect("write fixture");
    let result = parse_fixture(&path);
    fs::remove_file(&path).expect("remove fixture");
    assert!(result.is_err());
}
