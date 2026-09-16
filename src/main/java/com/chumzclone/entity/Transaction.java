package com.chumzclone.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private SavingsGoal goal; // nullable - null means wallet top-up not tied to a goal

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private SavingsGroup group; // nullable - set when this is a group contribution

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    private String mpesaReceiptNumber; // set from Daraja callback

    private String checkoutRequestId; // Daraja STK push tracking id

    private String triggerNote; // e.g. "Round-up nudge on Ksh 250 spend"

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, GROUP_CONTRIBUTION, INTEREST
    }

    public enum TransactionStatus {
        PENDING, SUCCESS, FAILED
    }
}
