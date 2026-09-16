package com.chumzclone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "challenges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. "52-Week Envelope Challenge"

    private String description;

    @Enumerated(EnumType.STRING)
    private ChallengeType type;

    private Integer durationDays;

    private BigDecimal startAmount; // for envelope-style challenges

    public enum ChallengeType {
        ENVELOPE_52_WEEK, ROUND_UP, DAILY_FIXED, CUSTOM
    }
}
