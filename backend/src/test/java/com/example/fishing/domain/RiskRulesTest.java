package com.example.fishing.domain;

import com.example.fishing.infrastructure.SeedDataRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RiskRulesTest {
    @Test void seededVesselsCoverHighMediumAndLow() {
        var repo = new SeedDataRepository();
        var rules = new RiskRules();
        assertEquals("HIGH", rules.assess(repo.vessels().get(0), repo.areas()).level());
        assertEquals("LOW", rules.assess(repo.vessels().get(1), repo.areas()).level());
        assertEquals("MEDIUM", rules.assess(repo.vessels().get(2), repo.areas()).level());
        assertEquals("MEDIUM", rules.assess(repo.vessels().get(3), repo.areas()).level());
    }
}
