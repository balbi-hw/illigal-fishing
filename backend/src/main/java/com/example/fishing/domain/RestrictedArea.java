package com.example.fishing.domain;

import java.util.List;

public record RestrictedArea(String id, String name, List<GeoPoint> polygon) {}
