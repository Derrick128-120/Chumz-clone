package com.chumzclone.repository;

import com.chumzclone.entity.SavingsGoal;
import com.chumzclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUserOrderByCreatedAtDesc(User user);
    List<SavingsGoal> findByUserAndStatus(User user, SavingsGoal.GoalStatus status);
}
