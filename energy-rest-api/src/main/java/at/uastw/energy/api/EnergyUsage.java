package at.uastw.energy.api;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "energy_usage")
public class EnergyUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private LocalDateTime hour;
    
    @Column(name = "community_produced", nullable = false, precision = 10, scale = 3)
    private BigDecimal communityProduced;
    
    @Column(name = "community_used", nullable = false, precision = 10, scale = 3)
    private BigDecimal communityUsed;
    
    @Column(name = "grid_used", nullable = false, precision = 10, scale = 3)
    private BigDecimal gridUsed;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public EnergyUsage() {}
    
    public EnergyUsage(LocalDateTime hour, BigDecimal communityProduced, BigDecimal communityUsed, BigDecimal gridUsed) {
        this.hour = hour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
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
    
    public BigDecimal getCommunityProduced() {
        return communityProduced;
    }
    
    public void setCommunityProduced(BigDecimal communityProduced) {
        this.communityProduced = communityProduced;
    }
    
    public BigDecimal getCommunityUsed() {
        return communityUsed;
    }
    
    public void setCommunityUsed(BigDecimal communityUsed) {
        this.communityUsed = communityUsed;
    }
    
    public BigDecimal getGridUsed() {
        return gridUsed;
    }
    
    public void setGridUsed(BigDecimal gridUsed) {
        this.gridUsed = gridUsed;
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