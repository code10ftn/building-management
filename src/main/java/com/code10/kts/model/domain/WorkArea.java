package com.code10.kts.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a company's work area.
 */
public enum WorkArea {
    @JsonProperty("WATER")
    WATER,
    @JsonProperty("HEATING")
    HEATING,
    @JsonProperty("ELECTRICITY")
    ELECTRICITY,
    @JsonProperty("ELEVATORS")
    ELEVATORS,
    @JsonProperty("WINDOWS")
    WINDOWS,
    @JsonProperty("DOORS")
    DOORS,
    @JsonProperty("FLOORING")
    FLOORING,
    @JsonProperty("HOUSEKEEPING")
    HOUSEKEEPING
}
