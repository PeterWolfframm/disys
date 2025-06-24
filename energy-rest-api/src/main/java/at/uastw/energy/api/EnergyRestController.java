package at.uastw.energy.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.time.Instant;
import java.util.Map;

@RestController
public class EnergyRestController {
    private final Random random = new Random();
    
    @Autowired
    private EnergyDataRepository energyDataRepository;

    @GetMapping("/energy/current")
    public double getCurrentEnergy() {
        // For testing purposes, random number between 0 and 100
        return random.nextDouble() * 100;
    }

    @GetMapping("/energy/historic")
    public List<Map<String, Object>> getHistoricEnergy() {
        return Arrays.asList(
            Map.of("created", Instant.now().minusSeconds(3600 * 5).toString(), "leistung", 45.7),
            Map.of("created", Instant.now().minusSeconds(3600 * 4).toString(), "leistung", 67.2),
            Map.of("created", Instant.now().minusSeconds(3600 * 3).toString(), "leistung", 82.1),
            Map.of("created", Instant.now().minusSeconds(3600 * 2).toString(), "leistung", 34.9),
            Map.of("created", Instant.now().minusSeconds(3600).toString(), "leistung", 91.3),
            Map.of("created", Instant.now().toString(), "leistung", 73.5)
        );
    }

    @PostMapping("/energy/data")
    public ResponseEntity<EnergyData> createEnergyData(@RequestBody EnergyDataRequest request) {
        try {
            EnergyData energyData = new EnergyData();
            
            if (request.getTimestamp() != null) {
                energyData.setTimestamp(request.getTimestamp());
            } else {
                energyData.setTimestamp(LocalDateTime.now());
            }
            
            energyData.setEnergyConsumption(request.getEnergyConsumption());
            
            EnergyData savedData = energyDataRepository.save(energyData);
            
            return new ResponseEntity<>(savedData, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
} 