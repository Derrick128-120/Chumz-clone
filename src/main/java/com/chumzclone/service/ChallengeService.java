package com.chumzclone.service;

import com.chumzclone.entity.*;
import com.chumzclone.repository.ChallengeRepository;
import com.chumzclone.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    public List<UserChallenge> getUserChallenges(User user) {
        return userChallengeRepository.findByUser(user);
    }

    public UserChallenge enroll(User user, Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found"));

        UserChallenge uc = UserChallenge.builder()
                .user(user)
                .challenge(challenge)
                .build();
        return userChallengeRepository.save(uc);
    }

    /** Records this week/day's contribution towards an active challenge. */
    public UserChallenge logProgress(UserChallenge uc, BigDecimal amount) {
        uc.setTotalSaved(uc.getTotalSaved().add(amount));
        uc.setCurrentWeekOrDay(uc.getCurrentWeekOrDay() + 1);

        Integer duration = uc.getChallenge().getDurationDays();
        if (duration != null && uc.getCurrentWeekOrDay() > duration) {
            uc.setStatus(UserChallenge.Status.COMPLETED);
        }
        return userChallengeRepository.save(uc);
    }
}
