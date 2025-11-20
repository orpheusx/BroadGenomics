package org.broadinstitute;

import java.util.*;

/**
 * An implementation that provides answers to three questions using the MBTA API.
 * Results are written to stdout.
 */
public class AnswersForQuestions {

    private List<Route> routes;
    private final Map<String, Set<String>> routesByStopName = new HashMap<>();

    /**
     * Provides: '... a program that retrieves data representing all, what we'll call "subway" routes: "Light Rail" (type
     * 0) and “Heavy Rail” (type 1). The program should list their “long names” on the console.'
     */
    public void questionOne() {
        routes = APIClient.fetchAllRoutes();
        System.out.println("\nQuestion 1:");
        routes.forEach(route -> {
            System.out.println(route.attributes().long_name() + ": " + route.id()); // Might add a check for null and print error message
        });

    }

    /**
     * Provides:
     * 1. The name of the subway route with the most stops as well as a count of its stops.
     * 2. The name of the subway route with the fewest stops as well as a count of its stops.
     * 3. A list of the stops that connect two or more subway routes along with the relevant route
     *      names for each of those stops.
     * <p>
     *  The /stops API can include the route information for each stop but requires that the request includes filter[route] with the id
     *  of a single route. So it's not usable for discovery purposes. We'll use the list of routes produced in questionOne to fetch the stop
     *  information for each of them.
     */
    public void questionTwo() {
        Route mostStops = null;
        Route leastStops = null;
        int maxStops = 0;
        int minStops = 0;

        for (Route route : routes) {
            final List<Stop> routeStops = APIClient.fetchStopsForRoute(route.id());

            if (mostStops == null || routeStops.size() > maxStops) {
                mostStops = route;
                maxStops = routeStops.size();
            }
            if (leastStops == null || routeStops.size() < minStops) {
                leastStops = route;
                minStops = routeStops.size();
            }
            for (Stop stop : routeStops) {
                Set<String> names = routesByStopName.computeIfAbsent(stop.attributes().name(), k -> new HashSet<>());
                names.add(route.attributes().long_name());
            }
        }

        System.out.println("\nQuestion 2:");

        // TODO solve the tie between Green-E and Green-D.
        System.out.println("Most stops: " + mostStops.attributes().long_name() + ": " + maxStops);
        System.out.println("Least stops: " + leastStops.attributes().long_name() + ": " + minStops);

        System.out.println("Connecting Stops: ");
        for (Map.Entry<String, Set<String>> entry : routesByStopName.entrySet()) {
            if (entry.getValue().size() > 1) {
                System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    /**
     * Provides:
     * [a] program [...] such that the user can provide any two stops on the subway routes you
     * listed for question 1.
     * List a rail route you could travel to get from one stop to the other. We will not evaluate your solution
     * based upon the efficiency or cleverness of your route-finding solution. Pick a simple solution that
     * answers the question. We will want you to understand and be able to explain how your algorithm
     * performs.
     * Examples:
     * 1. Davis to Kendall/MIT -> Red Line
     * 2. Ashmont to Arlington -> Red Line, Green Line B
     * <p>
     * NB: The /stops API provides a "connecting_stops" relationship that sounded useful in the context of this question.
     * These appear to be only bus connections, however.
     */
    public void questionThree(String stopId1, String stopId2) {
        // TODO validate input?
        System.out.println("\nQuestion 3:");

        final Set<String> routes1 = routesByStopName.get(stopId1);
        final Set<String> routes2 = routesByStopName.get(stopId2);

        // Is there a route in common between the two lists?
        for (String route : routes1) {
            if (routes2.contains(route)) {
                // ah, cool! they're both on the same route
                found(stopId1, stopId2, List.of(route));
                return;
            }
        }

        // Is there a different stop that has one of the routes in both stops in common?
        for (Map.Entry<String, Set<String>> entry : routesByStopName.entrySet()) {
            String stopName = entry.getKey();
            Set<String> routesAtStop = entry.getValue();
            if (routesAtStop.size() > 1) { // only check stops that join 2+ routes
                String match1 = intersection(routesAtStop, routes1);
                String match2 = intersection(routesAtStop, routes2);
                if (match1 != null && match2 != null) {
                    found(stopId1, stopId2, List.of(match1, match2));
                    System.out.println("\tConnecting stop: " + stopName);
                    return;
                }
            }
        }
        System.out.println("Hmm, didn't find a solution.");
    }

    private String intersection(Set<String> setA, Set<String> setB) {
        for (String a : setA) {
            if (setB.contains(a)) {
                return a;
            }
        }
        return null;
    }

    private void found(String stopId1, String stopId2, List<String> commonRoute) {
        System.out.println("\t" + stopId1 + " to " + stopId2 + " -> " + commonRoute);
    }

    /**
     * Execute all three questions.
     * NB: Some of the data structures are reused between question methods.
     * @param args no arguments are expected.
     */
    public static void main(String[] args) {
        AnswersForQuestions answer = new AnswersForQuestions();
        answer.questionOne();
        answer.questionTwo();
        answer.questionThree("Davis", "Kendall/MIT");
        answer.questionThree("Ashmont", "Arlington");
    }

}
