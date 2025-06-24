package at.uastw.energy.percentageservice.service;

import at.uastw.energy.percentageservice.config.RabbitMQConfig;
import at.uastw.energy.percentageservice.model.EnergyPercentage;
import at.uastw.energy.percentageservice.model.EnergyUsage;
import at.uastw.energy.percentageservice.repository.EnergyPercentageRepository;
import at.uastw.energy.percentageservice.repository.EnergyUsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class PercentageService {

    private static final Logger log = LoggerFactory.getLogger(PercentageService.class);

    private final EnergyUsageRepository usageRepository;
    private final EnergyPercentageRepository percentageRepository;

    public PercentageService(EnergyUsageRepository usageRepository, EnergyPercentageRepository percentageRepository) {
        this.usageRepository = usageRepository;
        this.percentageRepository = percentageRepository;
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.USAGE_UPDATE_QUEUE)
    public void calculateAndStorePercentage(LocalDateTime hour) {
        log.info("Received usage update for hour: {}", hour);

        usageRepository.findByHour(hour).ifPresentOrElse(
            usage -> {
                BigDecimal communityDepleted = calculateCommunityDepletion(usage);
                BigDecimal gridPortion = calculateGridPortion(usage);

                EnergyPercentage percentage = percentageRepository.findByHour(hour)
                        .orElse(new EnergyPercentage(hour, communityDepleted, gridPortion));

                percentage.setCommunityDepleted(communityDepleted);
                percentage.setGridPortion(gridPortion);

                percentageRepository.save(percentage);
                log.info("Saved percentage for hour {}: Depleted={}, GridPortion={}", hour, communityDepleted, gridPortion);
            },
            () -> log.error("Could not find energy usage data for hour: {}", hour)
        );
    }

    private BigDecimal calculateCommunityDepletion(EnergyUsage usage) {
        if (usage.getCommunityProduced().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // (communityUsed / communityProduced) * 100
        BigDecimal depletion = usage.getCommunityUsed()
                .divide(usage.getCommunityProduced(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        // Cap at 100%
        return depletion.min(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateGridPortion(EnergyUsage usage) {
        BigDecimal totalUsage = usage.getCommunityUsed().add(usage.getGridUsed());
        if (totalUsage.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // (gridUsed / (communityUsed + gridUsed)) * 100
        return usage.getGridUsed()
                .divide(totalUsage, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
} 