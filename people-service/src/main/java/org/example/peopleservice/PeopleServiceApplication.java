package org.example.peopleservice;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "org.example")
@AutoConfigurationPackage(basePackages = "org.example")
@EnableFeignClients
@EnableJpaAuditing   // activates @CreatedDate / @LastModifiedDate auditing
@EnableAsync         // for future async indexing
@EnableKafka         // activates Kafka producer/consumer
@Tag(name = "People Service", description = "Actors, directors, writers, and all cast/crew credits")
public class PeopleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeopleServiceApplication.class, args);
    }
}
