package org.example.userservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String country;
    private LocalDate birthDate;
    private String gender;
    private String websiteUrl;
    private Integer totalRatings;
    private Integer totalReviews;

    @CreatedDate
    private Instant memberSince;
}
