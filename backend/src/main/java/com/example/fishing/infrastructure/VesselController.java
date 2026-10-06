package com.example.fishing.infrastructure;

import com.example.fishing.application.VesselService;
import com.example.fishing.domain.RestrictedArea;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class VesselController {
    private final VesselService service;
    public VesselController(VesselService service) { this.service = service; }
    @GetMapping("/vessels") public List<VesselService.VesselResult> vessels() { return service.vessels(); }
    @GetMapping("/vessels/{mmsi}") public VesselService.VesselResult vessel(@PathVariable String mmsi) { return service.vessel(mmsi); }
    @GetMapping("/restricted-areas") public List<RestrictedArea> areas() { return service.areas(); }
    @ExceptionHandler(VesselService.VesselNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(RuntimeException ex) { return Map.of("error", ex.getMessage()); }
}
