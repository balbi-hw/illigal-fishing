package com.example.fishing.infrastructure;

import com.example.fishing.domain.*;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public class SeedDataRepository {
    private final List<RestrictedArea> areas = List.of(new RestrictedArea("R-1", "시범 제한 해역", List.of(
            new GeoPoint(34.25, 125.55), new GeoPoint(34.25, 126.05),
            new GeoPoint(34.65, 126.05), new GeoPoint(34.65, 125.55))));
    private final List<Vessel> vessels = List.of(
            vessel("440123456", "청해 1호", new double[][]{{34.35,125.67,3.1},{34.40,125.78,2.9},{34.36,125.86,2.4},{34.43,125.93,2.7},{34.39,125.82,2.5}}),
            vessel("440234567", "남해 7호", new double[][]{{34.81,125.46,10.2},{34.77,125.58,10.6},{34.73,125.70,10.4},{34.69,125.82,10.8}}),
            vessel("440345678", "푸른바다", new double[][]{{34.21,126.14,3.2},{34.18,126.22,2.8},{34.22,126.30,2.6},{34.18,126.38,3.0}}),
            vessel("440456789", "해오름", new double[][]{{34.52,125.72,8.4},{34.53,125.77,7.9},{34.54,125.82,8.1},{34.55,125.87,8.5}}));

    private static Vessel vessel(String mmsi, String name, double[][] rows) {
        Instant start = Instant.parse("2026-10-06T00:00:00Z");
        List<TrackPoint> points = java.util.stream.IntStream.range(0, rows.length)
                .mapToObj(i -> new TrackPoint(start.plusSeconds(i * 600L), new GeoPoint(rows[i][0], rows[i][1]), rows[i][2])).toList();
        return new Vessel(mmsi, name, points);
    }
    public List<Vessel> vessels() { return vessels; }
    public List<RestrictedArea> areas() { return areas; }
}
