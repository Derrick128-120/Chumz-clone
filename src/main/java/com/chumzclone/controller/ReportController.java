package com.chumzclone.controller;

import com.chumzclone.entity.Transaction;
import com.chumzclone.entity.User;
import com.chumzclone.repository.TransactionRepository;
import com.chumzclone.security.CustomUserDetails;
import com.chumzclone.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final TransactionRepository transactionRepository;
    private final SavingsGoalService goalService;

    @GetMapping
    public String reports(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        User user = principal.getUser();
        List<Transaction> transactions = transactionRepository.findByUserOrderByCreatedAtDesc(user);

        BigDecimal totalSaved = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.DEPOSIT
                        || t.getType() == Transaction.TransactionType.GROUP_CONTRIBUTION)
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.SUCCESS)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("transactions", transactions);
        model.addAttribute("totalSaved", totalSaved);
        model.addAttribute("goals", goalService.getGoalsForUser(user));
        model.addAttribute("user", user);
        return "reports/summary";
    }
}
