package at.uastw.energy.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class EnergyRestController {
    
    @Autowired
    private EnergyPercentageRepository energyPercentageRepository;
    
    @Autowired
    private EnergyUsageRepository energyUsageRepository;

    /**
     * GET /energy/current
     * Returns a single double value representing the latest total power usage,
     * for compatibility with the existing JavaFX client.
     */
    @GetMapping("/energy/current")
    public ResponseEntity<Double> getCurrentEnergy() {
        Optional<EnergyUsage> currentUsage = energyUsageRepository.findFirstByOrderByHourDesc();

        if (currentUsage.isPresent()) {
            EnergyUsage usage = currentUsage.get();
            // Returning total consumption (community + grid) as a "power" value for the client
            double totalPower = usage.getCommunityUsed().add(usage.getGridUsed()).doubleValue();
            return ResponseEntity.ok(totalPower);
        } else {
            return ResponseEntity.ok(0.0); // Return 0 if no data is available
        }
    }

    /**
     * GET /energy/historic
     * Returns usage data from the last 24 hours for compatibility with the existing JavaFX client.
     * The original endpoint was /energy/historical?start=...&end=...
     */
    @GetMapping("/energy/historic")
    public ResponseEntity<List<LegacyEnergyData>> getHistoricalEnergy() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusHours(24);

        List<EnergyUsage> usageData = energyUsageRepository.findByHourBetween(start, end);
        
        List<LegacyEnergyData> response = usageData.stream()
            .map(usage -> new LegacyEnergyData(
                usage.getHour().toInstant(ZoneOffset.UTC),
                usage.getCommunityUsed().add(usage.getGridUsed()).doubleValue()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    // --- NEW SPEC-COMPLIANT ENDPOINTS ---

    /**
     * GET /api/energy/current
     * Returns the percentage data of the current hour
     */
    @GetMapping("/api/energy/current")
    public ResponseEntity<CurrentPercentageResponse> getCurrentEnergyPercentage() {
        Optional<EnergyPercentage> currentPercentage = energyPercentageRepository.findCurrentHourPercentage();
        
        if (currentPercentage.isPresent()) {
            EnergyPercentage percentage = currentPercentage.get();
            CurrentPercentageResponse response = new CurrentPercentageResponse(
                percentage.getHour(),
                percentage.getCommunityDepleted(),
                percentage.getGridPortion()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * GET /api/energy/historical?start=...&end=...
     * Returns the usage data for a given time period (start to end)
     */
    @GetMapping("/api/energy/historical")
    public ResponseEntity<List<HistoricalUsageResponse>> getHistoricalEnergyUsage(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        if (start.isAfter(end)) {
            return ResponseEntity.badRequest().build();
        }
        
        List<EnergyUsage> usageData = energyUsageRepository.findByHourBetween(start, end);
        
        List<HistoricalUsageResponse> response = usageData.stream()
            .map(usage -> new HistoricalUsageResponse(
                usage.getHour(),
                usage.getCommunityProduced(),
                usage.getCommunityUsed(),
                usage.getGridUsed()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
} 