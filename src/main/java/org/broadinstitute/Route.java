package org.broadinstitute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Route(String id, String type, RouteAttributes attributes) {}
