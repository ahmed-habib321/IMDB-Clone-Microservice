package org.example.contributionservice;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "org.example")
@AutoConfigurationPackage(basePackages = "org.example")
@EnableJpaAuditing
@Tag(name = "Contribution Service", description = "Community-submitted content — trivia, goofs, and quotes — pending moderation before display.")
public class ContributionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContributionServiceApplication.class, args);
    }


}
