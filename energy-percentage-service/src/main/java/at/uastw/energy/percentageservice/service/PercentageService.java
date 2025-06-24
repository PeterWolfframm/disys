package at.uastw.energy.percentageservice.service;

import at.uastw.energy.percentageservice.config.RabbitMQConfig;
import at.uastw.energy.percentageservice.model.EnergyPercentage;
import at.uastw.energy.percentageservice.model.EnergyUsage;
import at.uastw.energy.percentageservice.repository.EnergyPercentageRepository;
import at.uastw.energy.percentageservice.repository.EnergyUsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class PercentageService {

    private static final Logger log = LoggerFactory.getLogger(PercentageService.class);

    @Autowired
    private EnergyUsageRepository usageRepository;

    @Autowired
    private EnergyPercentageRepository percentageRepository;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.USAGE_UPDATE_QUEUE)
    public void receiveUsageUpdate(LocalDateTime hour) {
        log.info("Received usage update notification for hour: {}", hour);

        usageRepository.findByHour(hour).ifPresentOrElse(
                this::calculateAndSavePercentage,
                () -> log.warn("No usage data found for hour {}. Cannot calculate percentage.", hour)
        );
    }

    private void calculateAndSavePercentage(EnergyUsage usage) {
        BigDecimal communityProduced = usage.getCommunityProduced();
        BigDecimal communityUsed = usage.getCommunityUsed();
        BigDecimal gridUsed = usage.getGridUsed();

        BigDecimal communityDepleted = calculateCommunityDepleted(communityProduced, communityUsed);
        BigDecimal gridPortion = calculateGridPortion(communityUsed, gridUsed);

        EnergyPercentage percentage = percentageRepository.findByHour(usage.getHour())
                .orElseGet(() -> new EnergyPercentage(usage.getHour(), BigDecimal.ZERO, BigDecimal.ZERO));

        percentage.setCommunityDepleted(communityDepleted);
        percentage.setGridPortion(gridPortion);

        percentageRepository.save(percentage);
        log.info("Saved new percentage for hour {}: Community Depleted = {}%, Grid Portion = {}%",
                usage.getHour(), communityDepleted, gridPortion);
    }

    private BigDecimal calculateCommunityDepleted(BigDecimal produced, BigDecimal used) {
        if (produced.compareTo(BigDecimal.ZERO) == 0) {
            return used.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("100.00") : BigDecimal.ZERO;
        }
        BigDecimal depletion = used.divide(produced, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        return depletion.min(new BigDecimal("100.00")).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateGridPortion(BigDecimal communityUsed, BigDecimal gridUsed) {
        BigDecimal totalUsed = communityUsed.add(gridUsed);
        if (totalUsed.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return gridUsed.divide(totalUsed, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }
} 