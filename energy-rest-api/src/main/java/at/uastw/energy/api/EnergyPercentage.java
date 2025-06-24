package at.uastw.energy.api;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "energy_percentage")
public class EnergyPercentage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private LocalDateTime hour;
    
    @Column(name = "community_depleted", nullable = false, precision = 5, scale = 2)
    private BigDecimal communityDepleted;
    
    @Column(name = "grid_portion", nullable = false, precision = 5, scale = 2)
    private BigDecimal gridPortion;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public EnergyPercentage() {}
    
    public EnergyPercentage(LocalDateTime hour, BigDecimal communityDepleted, BigDecimal gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getHour() {
        return hour;
    }
    
    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }
    
    public BigDecimal getCommunityDepleted() {
        return communityDepleted;
    }
    
    public void setCommunityDepleted(BigDecimal communityDepleted) {
        this.communityDepleted = communityDepleted;
    }
    
    public BigDecimal getGridPortion() {
        return gridPortion;
    }
    
    public void setGridPortion(BigDecimal gridPortion) {
        this.gridPortion = gridPortion;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 