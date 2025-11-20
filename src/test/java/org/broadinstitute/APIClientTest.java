package org.broadinstitute;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ideally we'd mock out some of the machinery calling the API, but I'm running short on time and typically involve
 * additional libraries. So for now, I'll just sanity-check some of the data returned. This makes the tests subject to the MBTA
 * API's availability and token limits which is uncool.
 */
public class APIClientTest {

    @Test
    void fetchAllRoutes() {
        final List<Route> routes = APIClient.fetchAllRoutes();
        assertFalse(routes.isEmpty());

        Route orangeLine = new Route("Orange", "route", new RouteAttributes("Orange Line"));
        assertTrue(routes.contains(orangeLine));
    }

    @Test
    void fetchStopsForRoute() {
        final List<Stop> orangeLineStops = APIClient.fetchStopsForRoute("Orange");
        assertFalse(orangeLineStops.isEmpty());
        //orangeLineStops.forEach(System.out::println);

        Stop maldenCenter = new Stop("place-mlmnl", new StopAttributes("Malden Center"));
        assertTrue(orangeLineStops.contains(maldenCenter));

        Stop wellington = new Stop("place-welln", new StopAttributes("Wellington"));
        assertTrue(orangeLineStops.contains(wellington));

        Stop symphony = new Stop("place-symcl", new StopAttributes("Symphony"));
        assertFalse(orangeLineStops.contains(symphony));

        final List<Stop> greenLineStops = APIClient.fetchStopsForRoute("Green-E");
        //greenLineStops.forEach(System.out::println);

        assertTrue(greenLineStops.contains(symphony));


    }

}