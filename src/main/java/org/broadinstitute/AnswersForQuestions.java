package org.broadinstitute;

import java.util.*;

/**
 * An implementation that provides answers to three questions using the MBTA API.
 *
 */
public class AnswersForQuestions {

    static final String MBTA_ROUTES_MIN        = "https://api-v3.mbta.com/routes?fields[route]=long_name,type&filter[type]=0,1";
//    static final String MBTA_ROUTES_WITH_STOPS = "https://api-v3.mbta.com/routes?fields[route]=long_name,type&filter[type]=0,1&filter[stop]&include=stop";
    static final String MBTA_ROUTES_WITH_STOPS = "https://api-v3.mbta.com/routes?fields[route]=long_name,type&filter[type]=0,1&include=stop&filter[stop]=place-cntsq";
    //    static final String MBTA_ROUTES_WITH_STOPS = "https://api-v3.mbta.com/routes?fields[route]=long_name,type&include=stop&filter[stop]";

//    static final String MBTA_ROUTES = "https://api-v3.mbta.com/stops?filter[route]=Red";
    static final String STOPS_BY_ROUTE = "https://api-v3.mbta.com/stops?include=route&filter[route]="; // append the id of the route

    private List<Route> routes;
    private Map<Route, List<Stop>> stopsByRoute = new HashMap<>();

    /**
     * Provides: '... a program that retrieves data representing all, what we'll call "subway" routes: "Light Rail" (type
     * 0) and “Heavy Rail” (type 1). The program should list their “long names” on the console.'
     */
    public void questionOne() {
        routes = APIClient.fetchAllRoutes();
        System.out.println("Question One:");
        routes.forEach(route -> {
            System.out.println(route.attributes().long_name() + " (id: " + route.id() + ")"); // Might add a check for null and print error message
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

        Map<String, Set<String>> routesByStopName = new HashMap<>();

        for (Route route : routes) {
            final List<Stop> routeStops = APIClient.fetchStopsForRoute(route.id());
            stopsByRoute.put(route, routeStops); // for use in questionThree

            if (mostStops == null || routeStops.size() > maxStops) {
                mostStops = route;
                maxStops = routeStops.size();
            }
            if (leastStops == null || routeStops.size() < minStops) {
                leastStops = route;
                minStops = routeStops.size();
            }
            for (Stop stop : routeStops) {
                Set<String> names = routesByStopName.get(stop.attributes().name());
                if (names == null) {
                    names = new HashSet<>();
                    routesByStopName.put(stop.attributes().name(), names);
                }
                names.add(route.attributes().long_name());

            }
        }

        // FIXME solve the tie between Green-E and Green-D
        System.out.println("Most stops: " + mostStops.id() + ": " + maxStops);
        System.out.println("Least stops: " + leastStops.id() + ": " + minStops);

        System.out.println("Connected Stop: ");
        for (Map.Entry<String, Set<String>> entry : routesByStopName.entrySet()) {
            if (entry.getValue().size() > 1) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    /**
     * Provides:
     * "[a] program [...] such that the user can provide any two stops on the subway routes you
     * listed for question 1.
     * "List a rail route you could travel to get from one stop to the other. We will not evaluate your solution
     * based upon the efficiency or cleverness of your route-finding solution. Pick a simple solution that
     * answers the question. We will want you to understand and be able to explain how your algorithm
     * performs.
     * "Examples:
     * 1. Davis to Kendall/MIT -> Red Line
     * 2. Ashmont to Arlington -> Red Line, Green Line B"
     * <p>
     * NB: The /stops API provides a "connecting_stops" relationship that sounded useful in the context of this question.
     * These appear to be only bus connections, however.
     * NB: Note that route can only be included if filter[route] is present and has exactly one /data/{index}/relationships/route/data/id.
     * This would seem to require separate calls to fetch the stops for each route.
     */
    public void questionThree(String stopId1, String stopId2) {

    }

    public static void main(String[] args) {
        AnswersForQuestions answer = new AnswersForQuestions();
        answer.questionOne();
        answer.questionTwo();
    }

    record Attributes(String long_name, int type) {}

    record SimpleRoute(Attributes attributes) {
        /*
        * {
        *     "attributes": {
        *       "long_name": "Red Line",
        *       "type": 1
        * },
        *     "id": "Red",
        *     "links": {
        *       "self": "/routes/Red"
        *      },
        *     "relationships": {
        *       "agency": {
        *         "data": {
        *             "id": "1",
        *                     "type": "agency"
        *         }
        *     },
        *     "line": {
        *         "data": {
        *             "id": "line-Red",
        *                     "type": "line"
        *         }
        *     }
        * },
        *     "type": "route"
        * }
        */
    }
}
