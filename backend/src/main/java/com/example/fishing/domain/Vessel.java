package com.example.fishing.domain;

import java.util.List;

public record Vessel(String mmsi, String name, List<TrackPoint> track) {
    public TrackPoint latest() { return track.get(track.size() - 1); }
}
