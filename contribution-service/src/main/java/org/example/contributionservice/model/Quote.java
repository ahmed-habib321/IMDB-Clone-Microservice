package org.example.contributionservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "quotes",
        indexes = @Index(name = "idx_quotes_title", columnList = "title_id"))
@Getter @Setter
@NoArgsConstructor
@SuperBuilder
public class Quote extends Contribution {

    // body (inherited from Contribution) holds the quote text
    private String spokenBy;
}