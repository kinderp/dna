use std::fs;
use std::path::Path;

use crate::graph::validate_id;
use crate::{GeoPoint, RoadGraph};

/// Parsed fixture and expected ground truth.
#[derive(Clone, Debug)]
pub struct ReferenceScenario {
    pub id: String,
    pub graph: RoadGraph,
    pub origin: String,
    pub destination: String,
    pub expected_cost_metres: u64,
    pub expected_path: Vec<String>,
}

/// Parses one strict `TDNA_REFERENCE_GRAPH_V0` fixture.
///
/// # Errors
///
/// Unknown directives, duplicate declarations, invalid numbers and missing
/// required declarations are rejected with line context.
pub fn parse_fixture(path: &Path) -> Result<ReferenceScenario, String> {
    let contents =
        fs::read_to_string(path).map_err(|error| format!("{}: {error}", path.display()))?;
    let mut graph = RoadGraph::default();
    let mut version_seen = false;
    let mut scenario_id: Option<String> = None;
    let mut origin: Option<String> = None;
    let mut destination: Option<String> = None;
    let mut expected_cost: Option<u64> = None;
    let mut expected_path: Option<Vec<String>> = None;

    for (index, raw) in contents.lines().enumerate() {
        let line = raw.trim();
        if line.is_empty() || line.starts_with('#') {
            continue;
        }
        let parts: Vec<&str> = line.split_whitespace().collect();
        let result = (|| -> Result<(), String> {
            match parts.first().copied() {
                Some("TDNA_REFERENCE_GRAPH_V0") => {
                    require_arity(&parts, 1)?;
                    if version_seen {
                        Err("duplicate version header".to_owned())
                    } else {
                        version_seen = true;
                        Ok(())
                    }
                }
                Some("scenario") => {
                    require_arity(&parts, 2)?;
                    if scenario_id.is_some() {
                        Err("duplicate scenario declaration".to_owned())
                    } else {
                        validate_id(parts[1], "scenario")?;
                        scenario_id = Some(parts[1].to_owned());
                        Ok(())
                    }
                }
                Some("node") => {
                    require_arity(&parts, 4)?;
                    ensure_version(version_seen)?;
                    let latitude = parse_f64(parts[2], "latitude")?;
                    let longitude = parse_f64(parts[3], "longitude")?;
                    graph.add_node(parts[1], GeoPoint::new(latitude, longitude)?)
                }
                Some("road") => {
                    require_arity(&parts, 4)?;
                    ensure_version(version_seen)?;
                    graph.add_bidirectional_road(
                        parts[1],
                        parts[2],
                        parse_u64(parts[3], "road cost")?,
                    )
                }
                Some("query") => {
                    require_arity(&parts, 3)?;
                    if origin.is_some() {
                        Err("duplicate query declaration".to_owned())
                    } else {
                        validate_id(parts[1], "origin")?;
                        validate_id(parts[2], "destination")?;
                        origin = Some(parts[1].to_owned());
                        destination = Some(parts[2].to_owned());
                        Ok(())
                    }
                }
                Some("expect") => {
                    require_arity(&parts, 3)?;
                    if expected_cost.is_some() {
                        Err("duplicate expectation declaration".to_owned())
                    } else {
                        expected_cost = Some(parse_u64(parts[1], "expected cost")?);
                        expected_path = Some(parse_path(parts[2])?);
                        Ok(())
                    }
                }
                Some(directive) => Err(format!("unknown directive: {directive}")),
                None => Ok(()),
            }
        })();
        if let Err(error) = result {
            return Err(format!("{}:{}: {error}", path.display(), index + 1));
        }
    }

    let scenario = ReferenceScenario {
        id: scenario_id.ok_or_else(|| "fixture is missing scenario".to_owned())?,
        graph,
        origin: origin.ok_or_else(|| "fixture is missing query origin".to_owned())?,
        destination: destination
            .ok_or_else(|| "fixture is missing query destination".to_owned())?,
        expected_cost_metres: expected_cost
            .ok_or_else(|| "fixture is missing expected cost".to_owned())?,
        expected_path: expected_path.ok_or_else(|| "fixture is missing expected path".to_owned())?,
    };
    if !version_seen {
        return Err("fixture is missing version header".to_owned());
    }
    scenario.graph.require_node(&scenario.origin)?;
    scenario.graph.require_node(&scenario.destination)?;
    if scenario.expected_cost_metres == 0 || scenario.expected_path.len() < 2 {
        return Err("fixture expectation must contain a positive route".to_owned());
    }
    if scenario.expected_path.first().map(String::as_str) != Some(scenario.origin.as_str())
        || scenario.expected_path.last().map(String::as_str)
            != Some(scenario.destination.as_str())
    {
        return Err(
            "expected path must start at the query origin and end at the destination".to_owned(),
        );
    }
    for node in &scenario.expected_path {
        scenario.graph.require_node(node)?;
    }
    Ok(scenario)
}

fn parse_path(value: &str) -> Result<Vec<String>, String> {
    let mut path = Vec::new();
    for node in value.split(',') {
        validate_id(node, "expected path node")?;
        path.push(node.to_owned());
    }
    Ok(path)
}

fn parse_u64(value: &str, field: &str) -> Result<u64, String> {
    value
        .parse::<u64>()
        .map_err(|_| format!("{field} must be an unsigned integer"))
}

fn parse_f64(value: &str, field: &str) -> Result<f64, String> {
    value
        .parse::<f64>()
        .map_err(|_| format!("{field} must be a floating-point number"))
}

fn ensure_version(version_seen: bool) -> Result<(), String> {
    if version_seen {
        Ok(())
    } else {
        Err("version header must appear before graph data".to_owned())
    }
}

fn require_arity(parts: &[&str], expected: usize) -> Result<(), String> {
    if parts.len() == expected {
        Ok(())
    } else {
        let directive = parts.first().copied().unwrap_or("<empty>");
        Err(format!(
            "directive {directive} expects {} argument(s)",
            expected.saturating_sub(1)
        ))
    }
}
