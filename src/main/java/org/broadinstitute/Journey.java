package org.broadinstitute;

import java.util.List;

/**
 * A record that contains the results of Question 3.
 * @param origin
 * @param destination
 * @param routes
 * @param connectingStop
 */
public record Journey(String origin, String destination, List<String> routes, String connectingStop) {}
