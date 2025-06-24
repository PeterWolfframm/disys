package at.uastw.energy.user.service;

import at.uastw.energy.user.model.EnergyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EnergyUserService {

    private static final Logger log = LoggerFactory.getLogger(EnergyUserService.class);

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    public EnergyUserService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 7000) // Sends a message every 7 seconds
    public void sendEnergyMessage() {
        double kwh = generateKwhForCurrentTime();

        EnergyMessage message = new EnergyMessage(
                "USER",
                "COMMUNITY",
                kwh,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(queueName, message);
        log.info("Sent message: {}", message);
    }

    private double generateKwhForCurrentTime() {
        int hour = LocalDateTime.now().getHour();
        // Peak hours: 7-9 and 18-21
        boolean isPeakHour = (hour >= 7 && hour < 10) || (hour >= 18 && hour < 22);

        if (isPeakHour) {
            // Higher consumption during peak hours, e.g., 0.002 to 0.006 kWh
            return 0.002 + (0.004 * random.nextDouble());
        } else {
            // Lower consumption during off-peak hours, e.g., 0.0005 to 0.0015 kWh
            return 0.0005 + (0.001 * random.nextDouble());
        }
    }
} 