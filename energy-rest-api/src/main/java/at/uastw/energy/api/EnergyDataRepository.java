package at.uastw.energy.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnergyDataRepository extends JpaRepository<EnergyData, Long> {
} 