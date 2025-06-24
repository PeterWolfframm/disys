package at.uastw.energy.usageservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ENERGY_QUEUE = "energy-queue";
    public static final String USAGE_UPDATE_QUEUE = "usage-update-queue";

    @Bean
    public Queue energyQueue() {
        return new Queue(ENERGY_QUEUE, true);
    }

    @Bean
    public Queue usageUpdateQueue() {
        return new Queue(USAGE_UPDATE_QUEUE, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
} 