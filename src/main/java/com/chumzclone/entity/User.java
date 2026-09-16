package com.chumzclone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber; // format 2547XXXXXXXX, used for STK push

    @Column(nullable = false)
    private String password; // BCrypt hash

    @Builder.Default
    private String role = "ROLE_USER";

    @Builder.Default
    private BigDecimal walletBalance = BigDecimal.ZERO;

    @Builder.Default
    private Integer currentStreakDays = 0;

    @Builder.Default
    private Integer longestStreakDays = 0;

    private LocalDateTime lastSaveDate;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private boolean enabled = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SavingsGoal> savingsGoals = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
