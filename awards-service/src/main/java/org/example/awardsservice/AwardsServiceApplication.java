package org.example.awardsservice;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.example")
@Tag(name = "Awards Service", description = "Award bodies and nomination/win records linking titles and people.")
public class AwardsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwardsServiceApplication.class, args);
    }

}
