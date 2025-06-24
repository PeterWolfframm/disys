package at.uastw.energy.api;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "energy_data")
public class EnergyData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "energy_consumption", nullable = false, precision = 10, scale = 2)
    private BigDecimal energyConsumption;
    
    public EnergyData() {}
    
    public EnergyData(LocalDateTime timestamp, BigDecimal energyConsumption) {
        this.timestamp = timestamp;
        this.energyConsumption = energyConsumption;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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