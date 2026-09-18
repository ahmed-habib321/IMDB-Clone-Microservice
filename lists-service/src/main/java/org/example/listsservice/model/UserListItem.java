package org.example.listsservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_list_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"list_id", "title_id"}))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private UserList list;

    @Column(name = "title_id", nullable = false)
    private UUID titleId;

    private Instant addedAt;
    private String notes;
}
