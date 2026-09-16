package com.chumzclone.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "savings_goals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String name; // e.g. "Emergency Fund", "New Phone"

    private String icon; // emoji or icon key shown on the neumorphic card

    @Column(nullable = false)
    private BigDecimal targetAmount;

    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;

    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GoalStatus status = GoalStatus.ACTIVE;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum GoalStatus {
        ACTIVE, COMPLETED, PAUSED
    }

    @Transient
    public double getProgressPercent() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) return 0;
        return currentAmount.divide(targetAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }
}
