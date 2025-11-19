package org.broadinstitute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Stop(String id, StopAttributes attributes) {
}
