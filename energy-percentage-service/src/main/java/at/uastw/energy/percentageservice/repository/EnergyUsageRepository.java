package at.uastw.energy.percentageservice.repository;

import at.uastw.energy.percentageservice.model.EnergyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EnergyUsageRepository extends JpaRepository<EnergyUsage, Long> {
    Optional<EnergyUsage> findByHour(LocalDateTime hour);
} 