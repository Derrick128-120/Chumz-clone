package com.chumzclone.config;

import com.chumzclone.entity.Challenge;
import com.chumzclone.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ChallengeRepository challengeRepository;

    @Override
    public void run(String... args) {
        if (challengeRepository.count() > 0) return;

        challengeRepository.save(Challenge.builder()
                .name("52-Week Envelope Challenge")
                .description("Save an increasing amount each week for a year - starts small, builds a habit.")
                .type(Challenge.ChallengeType.ENVELOPE_52_WEEK)
                .durationDays(52)
                .startAmount(BigDecimal.valueOf(50))
                .build());

        challengeRepository.save(Challenge.builder()
                .name("Round-Up Challenge")
                .description("Every M-Pesa spend gets rounded up to the nearest 50 bob, and the change is saved.")
                .type(Challenge.ChallengeType.ROUND_UP)
                .durationDays(30)
                .build());

        challengeRepository.save(Challenge.builder()
                .name("Daily Fixed Ksh 100")
                .description("Commit to saving a fixed Ksh 100 every single day for a month.")
                .type(Challenge.ChallengeType.DAILY_FIXED)
                .durationDays(30)
                .startAmount(BigDecimal.valueOf(100))
                .build());
    }
}
