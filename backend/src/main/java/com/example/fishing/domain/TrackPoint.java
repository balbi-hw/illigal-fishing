package com.example.fishing.domain;

import java.time.Instant;

public record TrackPoint(Instant time, GeoPoint position, double speedKnots) {}
