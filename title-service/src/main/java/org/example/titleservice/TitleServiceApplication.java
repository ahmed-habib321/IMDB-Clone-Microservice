package org.example.titleservice;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = "org.example")
@AutoConfigurationPackage(basePackages = "org.example")
@EnableCaching
@EnableJpaAuditing
@EnableKafka
@Tag(name = "Title Service",description = "Core catalogue of all movies and TV shows, including genres, keywords, certifications, languages, countries, seasons, and episodes")
public class TitleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TitleServiceApplication.class, args);
    }

}
