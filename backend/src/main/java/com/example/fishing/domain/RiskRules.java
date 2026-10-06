package com.example.fishing.domain;

import java.util.ArrayList;
import java.util.List;

public class RiskRules {
    public RiskAssessment assess(Vessel vessel, List<RestrictedArea> areas) {
        List<TrackPoint> track = vessel.track();
        TrackPoint latest = vessel.latest();
        boolean inside = areas.stream().anyMatch(a -> contains(a.polygon(), latest.position()));
        long slow = track.stream().filter(p -> p.speedKnots() >= 1 && p.speedKnots() <= 5).count();
        int turns = 0;
        for (int i = 2; i < track.size(); i++) {
            GeoPoint a = track.get(i - 2).position(), b = track.get(i - 1).position(), c = track.get(i).position();
            double cross = (b.longitude() - a.longitude()) * (c.latitude() - b.latitude())
                    - (b.latitude() - a.latitude()) * (c.longitude() - b.longitude());
            if (Math.abs(cross) > 0.000001) turns++;
        }
        boolean suspected = track.size() >= 4 && slow >= 3 && turns >= 2;
        int score = 0;
        List<String> reasons = new ArrayList<>();
        if (inside && suspected) { score += 75; reasons.add("제한 해역 내 저속·반복 이동으로 조업 의심 (+75)"); }
        else {
            if (inside) { score += 30; reasons.add("제한 해역 진입 (+30)"); }
            if (suspected) { score += 25; reasons.add("저속·반복 이동으로 조업 의심 (+25)"); }
        }
        if (reasons.isEmpty()) reasons.add("현재 감지된 위험 규칙 없음");
        String level = score >= 70 ? "HIGH" : score >= 25 ? "MEDIUM" : "LOW";
        return new RiskAssessment(score, level, suspected, inside, List.copyOf(reasons));
    }

    // Ray casting; longitude is x and latitude is y. Boundary points are treated as inside.
    public boolean contains(List<GeoPoint> polygon, GeoPoint point) {
        boolean inside = false;
        for (int i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            GeoPoint a = polygon.get(j), b = polygon.get(i);
            double cross = (point.longitude() - a.longitude()) * (b.latitude() - a.latitude())
                    - (point.latitude() - a.latitude()) * (b.longitude() - a.longitude());
            if (Math.abs(cross) < 1e-10 && point.longitude() >= Math.min(a.longitude(), b.longitude())
                    && point.longitude() <= Math.max(a.longitude(), b.longitude())
                    && point.latitude() >= Math.min(a.latitude(), b.latitude())
                    && point.latitude() <= Math.max(a.latitude(), b.latitude())) return true;
            if ((a.latitude() > point.latitude()) != (b.latitude() > point.latitude())
                    && point.longitude() < (b.longitude() - a.longitude()) * (point.latitude() - a.latitude())
                    / (b.latitude() - a.latitude()) + a.longitude()) inside = !inside;
        }
        return inside;
    }
}
