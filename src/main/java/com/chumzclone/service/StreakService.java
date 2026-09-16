package com.chumzclone.service;

import com.chumzclone.entity.User;
import com.chumzclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tracks consecutive-day saving streaks, mirroring the gamified nudge
 * mechanic Chumz uses to encourage daily saving habits.
 */
@Service
@RequiredArgsConstructor
public class StreakService {

    private final UserRepository userRepository;

    public void recordSaveEvent(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastSave = user.getLastSaveDate() != null ? user.getLastSaveDate().toLocalDate() : null;

        if (lastSave == null || lastSave.isBefore(today.minusDays(1))) {
            // streak broken or first ever save
            user.setCurrentStreakDays(1);
        } else if (lastSave.isEqual(today.minusDays(1))) {
            // consecutive day
            user.setCurrentStreakDays(user.getCurrentStreakDays() + 1);
        }
        // if lastSave.isEqual(today) -> already saved today, streak unchanged

        if (user.getCurrentStreakDays() > user.getLongestStreakDays()) {
            user.setLongestStreakDays(user.getCurrentStreakDays());
        }

        user.setLastSaveDate(LocalDateTime.now());
        userRepository.save(user);
    }
}
