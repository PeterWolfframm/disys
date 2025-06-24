package at.uastw.energy.percentageservice.repository;

import at.uastw.energy.percentageservice.model.EnergyPercentage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EnergyPercentageRepository extends JpaRepository<EnergyPercentage, Long> {
    Optional<EnergyPercentage> findByHour(LocalDateTime hour);
} 