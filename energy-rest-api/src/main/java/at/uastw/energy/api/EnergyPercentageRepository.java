package at.uastw.energy.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EnergyPercentageRepository extends JpaRepository<EnergyPercentage, Long> {
    
    @Query("SELECT ep FROM EnergyPercentage ep WHERE ep.hour = (SELECT MAX(ep2.hour) FROM EnergyPercentage ep2)")
    Optional<EnergyPercentage> findCurrentHourPercentage();
    
    Optional<EnergyPercentage> findByHour(LocalDateTime hour);
} 