package org.broadinstitute;

import java.util.Map;
import java.util.Set;

/**
 * A record that contains the results of Question 2.
 * @param mostStops
 * @param maxStops
 * @param leastStops
 * @param minStops
 * @param routesByStopName
 */
public record StopInfo(
        Route mostStops, int maxStops, Route leastStops, int minStops,
        Map<String, Set<String>> routesByStopName) {
}
