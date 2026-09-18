package org.example.contributionservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.contributionservice.model.enums.GoofType;

@Entity
@Table(name = "goofs",   // ✅ was missing — without this Hibernate picks "Goof" or inherits nothing
        indexes = @Index(name = "idx_goofs_title", columnList = "title_id"))
@Getter @Setter
@NoArgsConstructor
@SuperBuilder
public class Goof extends Contribution {

    @Enumerated(EnumType.STRING)
    private GoofType goofType;
}
