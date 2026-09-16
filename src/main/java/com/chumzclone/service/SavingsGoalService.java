package com.chumzclone.service;

import com.chumzclone.entity.SavingsGoal;
import com.chumzclone.entity.Transaction;
import com.chumzclone.entity.User;
import com.chumzclone.repository.SavingsGoalRepository;
import com.chumzclone.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {

    private final SavingsGoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final StreakService streakService;

    public List<SavingsGoal> getGoalsForUser(User user) {
        return goalRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public SavingsGoal createGoal(User user, String name, String icon, BigDecimal targetAmount, LocalDate targetDate) {
        SavingsGoal goal = SavingsGoal.builder()
                .user(user)
                .name(name)
                .icon(icon)
                .targetAmount(targetAmount)
                .targetDate(targetDate)
                .build();
        return goalRepository.save(goal);
    }

    /**
     * Applies a successful deposit to a goal, updates balances and streaks.
     * Called once the Daraja STK push callback confirms payment (or immediately
     * for manual/demo deposits in the sandbox).
     */
    @Transactional
    public Transaction depositToGoal(User user, SavingsGoal goal, BigDecimal amount, String triggerNote) {
        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(SavingsGoal.GoalStatus.COMPLETED);
        }
        goalRepository.save(goal);

        Transaction txn = Transaction.builder()
                .user(user)
                .goal(goal)
                .amount(amount)
                .type(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.TransactionStatus.SUCCESS)
                .triggerNote(triggerNote)
                .build();
        transactionRepository.save(txn);

        streakService.recordSaveEvent(user);
        return txn;
    }

    public SavingsGoal getById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));
    }
}
