package at.uastw.energy.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EnergyDataRequest {
    private LocalDateTime timestamp;
    private BigDecimal energyConsumption;
    
    public EnergyDataRequest() {}
    
    public EnergyDataRequest(LocalDateTime timestamp, BigDecimal energyConsumption) {
        this.timestamp = timestamp;
        this.energyConsumption = energyConsumption;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public BigDecimal getEnergyConsumption() {
        return energyConsumption;
    }
    
    public void setEnergyConsumption(BigDecimal energyConsumption) {
        this.energyConsumption = energyConsumption;
    }
} 