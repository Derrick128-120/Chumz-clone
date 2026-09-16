package com.chumzclone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "savings_groups")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SavingsGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. "Njuguna Family Chama"

    private String description;

    @Column(nullable = false, unique = true)
    private String inviteCode; // shareable join code

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Builder.Default
    private BigDecimal groupTargetAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal groupCurrentAmount = BigDecimal.ZERO;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GroupMember> members = new ArrayList<>();
}
