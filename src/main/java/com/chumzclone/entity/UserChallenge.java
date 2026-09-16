package com.chumzclone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "user_challenges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Builder.Default
    private LocalDate startDate = LocalDate.now();

    @Builder.Default
    private BigDecimal totalSaved = BigDecimal.ZERO;

    @Builder.Default
    private Integer currentWeekOrDay = 1;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.IN_PROGRESS;

    public enum Status {
        IN_PROGRESS, COMPLETED, ABANDONED
    }
}
