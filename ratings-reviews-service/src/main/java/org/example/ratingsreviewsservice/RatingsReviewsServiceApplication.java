package org.example.ratingsreviewsservice;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = "org.example")
@AutoConfigurationPackage(basePackages = "org.example")
@EnableKafka         // activates Kafka producer
@EnableJpaAuditing
@Tag(name = "Ratings & Reviews Service", description = "User ratings (1-10 scores) and written reviews with helpfulness voting.")
public class RatingsReviewsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RatingsReviewsServiceApplication.class, args);
    }


}
