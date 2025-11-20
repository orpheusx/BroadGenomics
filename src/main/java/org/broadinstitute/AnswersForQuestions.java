package org.broadinstitute;

import java.util.*;

import static java.lang.System.out;

/**
 * An implementation that provides answers to three questions using the MBTA API.
 * Results are written to stdout.
 */
public class AnswersForQuestions {

    public static String NO_CONNECTING_STOP = "None";

    /**
     * Provides: '... a program that retrieves data representing all, what we'll call "subway" routes: "Light Rail" (type
     * 0) and “Heavy Rail” (type 1). The program should list their “long names” on the console.'
     */
    public List<Route> questionOne() {
        return APIClient.fetchAllRoutes();
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
    public StopInfo questionTwo() {
        Route mostStops = null;
        Route leastStops = null;
        int maxStops = 0;
        int minStops = 0;

        Map<String, Set<String>> routesByStopName = new HashMap<>();
        for (Route route : APIClient.fetchAllRoutes()) {
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

        return new StopInfo(mostStops, maxStops, leastStops, minStops, routesByStopName);
    }

    private Map<String, Set<String>> constructRoutesByStopName() {
        Map<String, Set<String>> routesByStopName = new HashMap<>();
        final List<Route> routes = APIClient.fetchAllRoutes();
        if (routes == null) {

        }
        for (Route route : routes) {
            final List<Stop> routeStops = APIClient.fetchStopsForRoute(route.id());
            for (Stop stop : routeStops) {
                Set<String> names = routesByStopName.computeIfAbsent(stop.attributes().name(), k -> new HashSet<>());
                names.add(route.attributes().long_name());
            }
        }
        return routesByStopName;
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
    public Journey questionThree(String stopId1, String stopId2) {
        // TODO validate input?

        Map<String, Set<String>> routesByStopName = constructRoutesByStopName();
        final Set<String> routes1 = routesByStopName.get(stopId1);
        final Set<String> routes2 = routesByStopName.get(stopId2);

        // Is there a route in common between the two lists?
        for (String route : routes1) {
            if (routes2.contains(route)) {
                // ah, cool! they're both on the same route
                return new Journey(stopId1, stopId2, List.of(route), NO_CONNECTING_STOP);
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
                    return new Journey(stopId1, stopId2, List.of(match1, match2), stopName);
                }
            }
        }
        return null;
    }

    private String intersection(Set<String> setA, Set<String> setB) {
        for (String a : setA) {
            if (setB.contains(a)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Execute all three questions.
     * @param args no arguments are expected.
     */
    public static void main(String[] args) throws InterruptedException {
        AnswersForQuestions answer = new AnswersForQuestions();

        final List<Route> routes = answer.questionOne();
        out.println("\nQuestion 1:");
        routes.forEach(route -> {
            out.println(route.attributes().long_name() + ": " + route.id()); // Might add a check for null and print error message
        });
        Thread.sleep(1000);

        StopInfo stopInfo = answer.questionTwo();
        out.println("\nQuestion 2:");
        // TODO solve the tie between Green-E and Green-D.
        out.println("Most stops: " + Objects.requireNonNull(stopInfo.mostStops()).attributes().long_name() + ": " + stopInfo.maxStops());
        out.println("Least stops: " + stopInfo.leastStops().attributes().long_name() + ": " + stopInfo.minStops());
        out.println("Connecting Stops: ");
        for (Map.Entry<String, Set<String>> entry : stopInfo.routesByStopName().entrySet()) {
            if (entry.getValue().size() > 1) {
                out.println("\t" + entry.getKey() + ": " + entry.getValue());
            }
        }

        Thread.sleep(1000);
        out.println("\nQuestion 3:");
        Journey davisToKendall = answer.questionThree("Davis", "Kendall/MIT");
        out.println("\t" + davisToKendall);
        Thread.sleep(5000);
        Journey ashmontToArlington = answer.questionThree("Ashmont", "Arlington");
        out.println("\t" + ashmontToArlington);
    }

}
