package at.uastw.energy.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistoricalUsageResponse {
    private LocalDateTime hour;
    private BigDecimal communityProduced;
    private BigDecimal communityUsed;
    private BigDecimal gridUsed;
    
    public HistoricalUsageResponse() {}
    
    public HistoricalUsageResponse(LocalDateTime hour, BigDecimal communityProduced, BigDecimal communityUsed, BigDecimal gridUsed) {
        this.hour = hour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
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
} 