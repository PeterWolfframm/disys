package at.uastw.energy.usageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnergyUsageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyUsageServiceApplication.class, args);
    }

} 