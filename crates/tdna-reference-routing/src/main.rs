use std::path::Path;

use tdna_reference_routing::{Algorithm, canonical_report, parse_fixture, route};

fn main() {
    let args: Vec<String> = std::env::args().collect();
    if args.len() != 3 {
        eprintln!("usage: tdna-reference-routing FIXTURE {{dijkstra|astar}}");
        std::process::exit(2);
    }
    let result = (|| -> Result<String, String> {
        let scenario = parse_fixture(Path::new(&args[1]))?;
        let algorithm = Algorithm::parse(&args[2])?;
        let route_result = route(
            &scenario.graph,
            &scenario.origin,
            &scenario.destination,
            algorithm,
        )?;
        Ok(canonical_report(&scenario, algorithm, &route_result))
    })();
    match result {
        Ok(report) => println!("{report}"),
        Err(error) => {
            eprintln!("reference-routing error: {error}");
            std::process::exit(1);
        }
    }
}
