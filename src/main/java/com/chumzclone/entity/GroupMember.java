package com.chumzclone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_members", uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private SavingsGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    private BigDecimal totalContributed = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberRole role = MemberRole.MEMBER;

    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();

    public enum MemberRole {
        ADMIN, MEMBER
    }
}
