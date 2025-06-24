package at.uastw.energy.producer.service;

import at.uastw.energy.producer.model.EnergyMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EnergyProducerService {

    private static final Logger log = LoggerFactory.getLogger(EnergyProducerService.class);

    private final RabbitTemplate rabbitTemplate;
    private final WebClient webClient;
    private final Random random = new Random();

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${weather.api.key}")
    private String weatherApiKey;

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    @Value("${weather.api.city}")
    private String city;


    public EnergyProducerService(RabbitTemplate rabbitTemplate, WebClient.Builder webClientBuilder) {
        this.rabbitTemplate = rabbitTemplate;
        this.webClient = webClientBuilder.baseUrl(weatherApiUrl).build();
    }

    @Scheduled(fixedRate = 5000) // Switched to 5 seconds for more frequent data
    public void sendEnergyMessage() {
        // Weather API call is temporarily disabled.
        // To re-enable, uncomment the line below and remove the random generation logic.
        // fetchWeatherAndProduceEnergy().block();

        // --- Start of temporary random data generation ---
        log.info("Generating random energy data because weather API is disabled.");
        double kwh = 0.001 + (0.005 - 0.001) * random.nextDouble(); // Random value between 0.001 and 0.005

        EnergyMessage message = new EnergyMessage(
                "PRODUCER",
                "COMMUNITY",
                kwh,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(queueName, message);
        log.info("Sent message: {}", message);
        // --- End of temporary random data generation ---
    }

    private Mono<Void> fetchWeatherAndProduceEnergy() {
        URI uri = UriComponentsBuilder.fromHttpUrl(weatherApiUrl)
                .queryParam("q", city)
                .queryParam("appid", weatherApiKey)
                .queryParam("units", "metric")
                .build()
                .toUri();

        return this.webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(WeatherResponse.class)
                .doOnSuccess(weatherResponse -> {
                    double cloudiness = weatherResponse.getClouds().getAll(); // Cloudiness percentage
                    // Max production (0.005 kWh) when cloudiness is 0%, min production (0.0005 kWh) when 100%
                    double baseProduction = 0.0005;
                    double maxProduction = 0.005;
                    double productionRange = maxProduction - baseProduction;
                    double producedKwh = maxProduction - (productionRange * (cloudiness / 100.0));

                    // Add a small random factor
                    producedKwh = producedKwh * (0.9 + 0.2 * random.nextDouble()); // +/- 10% randomization

                    EnergyMessage message = new EnergyMessage(
                            "PRODUCER",
                            "COMMUNITY",
                            Math.max(0, producedKwh), // ensure it's not negative
                            LocalDateTime.now()
                    );

                    rabbitTemplate.convertAndSend(queueName, message);
                    log.info("Sent message: {}", message);
                })
                .doOnError(error -> log.error("Error fetching weather data: {}", error.getMessage()))
                .then();
    }


    // DTO for Weather API Response
    private static class WeatherResponse {
        private Clouds clouds;

        public Clouds getClouds() {
            return clouds;
        }

        public void setClouds(Clouds clouds) {
            this.clouds = clouds;
        }
    }

    private static class Clouds {
        @JsonProperty("all")
        private int all;

        public int getAll() {
            return all;
        }

        public void setAll(int all) {
            this.all = all;
        }
    }
} 