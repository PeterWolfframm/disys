package at.uastw.energy.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
     * Returns the percentage data of the current hour
     */
    @GetMapping("/energy/current")
    public ResponseEntity<CurrentPercentageResponse> getCurrentEnergy() {
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
            // No data available - return empty response with appropriate status
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * GET /energy/historical?start=...&end=...
     * Returns the usage data for a given time period (start to end)
     */
    @GetMapping("/energy/historical")
    public ResponseEntity<List<HistoricalUsageResponse>> getHistoricalEnergy(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        // Validate parameters
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