package com.chumzclone.controller;

import com.chumzclone.entity.User;
import com.chumzclone.repository.TransactionRepository;
import com.chumzclone.security.CustomUserDetails;
import com.chumzclone.service.SavingsGoalService;
import com.chumzclone.service.SavingsGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final SavingsGoalService goalService;
    private final SavingsGroupService groupService;
    private final TransactionRepository transactionRepository;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        User user = principal.getUser();

        model.addAttribute("user", user);
        model.addAttribute("goals", goalService.getGoalsForUser(user));
        model.addAttribute("groups", groupService.getGroupsForUser(user));
        model.addAttribute("recentTransactions", transactionRepository.findTop10ByUserOrderByCreatedAtDesc(user));

        return "dashboard";
    }
}
