package org.example.listsservice;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "org.example")
@EnableJpaAuditing
@Tag(name = "Lists & Watchlist Service", description = "User-curated lists and the personal watchlist (with watched/unwatched tracking).")
public class ListsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ListsServiceApplication.class, args);
    }

}
