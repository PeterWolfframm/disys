package at.uastw.energy.usageservice.repository;

import at.uastw.energy.usageservice.model.EnergyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface EnergyUsageRepository extends JpaRepository<EnergyUsage, Long> {
    Optional<EnergyUsage> findByHour(LocalDateTime hour);
} 