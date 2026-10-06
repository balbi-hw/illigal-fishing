package com.example.fishing.domain;

import java.util.List;

public record RiskAssessment(int score, String level, boolean fishingSuspected, boolean insideRestrictedArea, List<String> reasons) {}
