package org.broadinstitute;

import java.util.List;

public record Journey(String origin, String destination, List<String> routes, String connectingStop) {}
