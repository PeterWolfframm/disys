package at.uastw.energy.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnergyUsageRepository extends JpaRepository<EnergyUsage, Long> {
    
    @Query("SELECT eu FROM EnergyUsage eu WHERE eu.hour BETWEEN :start AND :end ORDER BY eu.hour")
    List<EnergyUsage> findByHourBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    Optional<EnergyUsage> findByHour(LocalDateTime hour);

    Optional<EnergyUsage> findFirstByOrderByHourDesc();
} 