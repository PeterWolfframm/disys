package at.uastw.energy.usageservice.service;

import at.uastw.energy.usageservice.config.RabbitMQConfig;
import at.uastw.energy.usageservice.model.EnergyMessage;
import at.uastw.energy.usageservice.model.EnergyUsage;
import at.uastw.energy.usageservice.repository.EnergyUsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UsageService {

    private static final Logger log = LoggerFactory.getLogger(UsageService.class);

    @Autowired
    private EnergyUsageRepository energyUsageRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.ENERGY_QUEUE)
    public void receiveMessage(EnergyMessage message) {
        log.info("Received message: {}", message);

        LocalDateTime hour = message.getDatetime().truncatedTo(ChronoUnit.HOURS);
        BigDecimal kwh = BigDecimal.valueOf(message.getKwh());

        EnergyUsage energyUsage = energyUsageRepository.findByHour(hour)
                .orElseGet(() -> {
                    log.info("No existing usage data for hour {}, creating new entry.", hour);
                    return new EnergyUsage(hour, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                });

        if ("PRODUCER".equalsIgnoreCase(message.getType())) {
            energyUsage.setCommunityProduced(energyUsage.getCommunityProduced().add(kwh));
            log.info("Updated community production for {}: {}", hour, energyUsage.getCommunityProduced());
        } else if ("USER".equalsIgnoreCase(message.getType())) {
            BigDecimal communityProduced = energyUsage.getCommunityProduced();
            BigDecimal communityUsed = energyUsage.getCommunityUsed();
            BigDecimal availableCommunityEnergy = communityProduced.subtract(communityUsed);

            if (kwh.compareTo(availableCommunityEnergy) <= 0) {
                energyUsage.setCommunityUsed(communityUsed.add(kwh));
            } else {
                energyUsage.setCommunityUsed(communityUsed.add(availableCommunityEnergy));
                BigDecimal gridUsage = kwh.subtract(availableCommunityEnergy);
                energyUsage.setGridUsed(energyUsage.getGridUsed().add(gridUsage));
            }
            log.info("Updated community usage for {}: community={}, grid={}",
                    hour, energyUsage.getCommunityUsed(), energyUsage.getGridUsed());
        } else {
            log.warn("Unknown message type: {}", message.getType());
            return;
        }

        energyUsageRepository.save(energyUsage);
        log.info("Saved usage data for hour {}", hour);

        rabbitTemplate.convertAndSend(RabbitMQConfig.USAGE_UPDATE_QUEUE, hour);
        log.info("Sent usage update notification for hour {}", hour);
    }
} 