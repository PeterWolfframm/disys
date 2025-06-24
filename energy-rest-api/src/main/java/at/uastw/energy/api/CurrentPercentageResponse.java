package at.uastw.energy.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CurrentPercentageResponse {
    private LocalDateTime hour;
    private BigDecimal communityDepleted;
    private BigDecimal gridPortion;
    
    public CurrentPercentageResponse() {}
    
    public CurrentPercentageResponse(LocalDateTime hour, BigDecimal communityDepleted, BigDecimal gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
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
} 