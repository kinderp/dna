package org.traveldna.reference.routing;

import java.nio.file.Path;

/** Command-line entry point for the executable reference-routing Lab. */
public final class ReferenceRoutingCli {
    private ReferenceRoutingCli() {
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: ReferenceRoutingCli FIXTURE {dijkstra|astar}");
            System.exit(2);
        }
        try {
            final ReferenceScenario scenario = ReferenceFixtureParser.parse(Path.of(args[0]));
            final ReferenceRouter router = switch (args[1]) {
                case "dijkstra" -> new DijkstraRouter();
                case "astar" -> new AStarRouter();
                default -> throw new IllegalArgumentException("unknown algorithm: " + args[1]);
            };
            final RouteResult result = router.route(
                    scenario.graph(), scenario.origin(), scenario.destination());
            System.out.println(RouteReport.canonicalLine(scenario, router, result));
        } catch (Exception exception) {
            System.err.println("reference-routing error: " + exception.getMessage());
            System.exit(1);
        }
    }
}
