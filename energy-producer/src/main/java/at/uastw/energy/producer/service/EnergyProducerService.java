package at.uastw.energy.producer.service;

import at.uastw.energy.producer.model.EnergyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EnergyProducerService {

    private static final Logger log = LoggerFactory.getLogger(EnergyProducerService.class);

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    public EnergyProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void sendEnergyMessage() {
        log.info("Generating random energy data.");
        double kwh = 0.001 + (0.005 - 0.001) * random.nextDouble(); // Random value between 0.001 and 0.005

        EnergyMessage message = new EnergyMessage(
                "PRODUCER",
                "COMMUNITY",
                kwh,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(queueName, message);
        log.info("Sent message: {}", message);
    }
} 