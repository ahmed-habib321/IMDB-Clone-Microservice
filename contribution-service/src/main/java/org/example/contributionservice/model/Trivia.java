package org.example.contributionservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "trivia",
        indexes = @Index(name = "idx_trivia_title", columnList = "title_id"))
@Getter @Setter
@NoArgsConstructor
@SuperBuilder
public class Trivia extends Contribution {
    // All fields inherited from Contribution
}