package com.example.fishing.application;

import com.example.fishing.domain.*;
import com.example.fishing.infrastructure.SeedDataRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VesselService {
    private final SeedDataRepository repository;
    private final RiskRules rules = new RiskRules();
    public VesselService(SeedDataRepository repository) { this.repository = repository; }
    public List<VesselResult> vessels() { return repository.vessels().stream().map(this::result).toList(); }
    public VesselResult vessel(String mmsi) { return repository.vessels().stream().filter(v -> v.mmsi().equals(mmsi))
            .findFirst().map(this::result).orElseThrow(() -> new VesselNotFoundException(mmsi)); }
    public List<RestrictedArea> areas() { return repository.areas(); }
    private VesselResult result(Vessel vessel) { return new VesselResult(vessel.mmsi(), vessel.name(), vessel.latest(),
            vessel.track(), rules.assess(vessel, repository.areas())); }
    public record VesselResult(String mmsi, String name, TrackPoint latest, List<TrackPoint> track, RiskAssessment risk) {}
    public static class VesselNotFoundException extends RuntimeException {
        public VesselNotFoundException(String mmsi) { super("Unknown MMSI: " + mmsi); }
    }
}
