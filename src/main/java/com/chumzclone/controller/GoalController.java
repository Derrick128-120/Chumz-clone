package com.chumzclone.controller;

import com.chumzclone.entity.SavingsGoal;
import com.chumzclone.entity.User;
import com.chumzclone.security.CustomUserDetails;
import com.chumzclone.service.DarajaService;
import com.chumzclone.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Controller
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final SavingsGoalService goalService;
    private final DarajaService darajaService;

    @GetMapping
    public String list(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("goals", goalService.getGoalsForUser(principal.getUser()));
        return "goals/list";
    }

    @GetMapping("/new")
    public String createForm() {
        return "goals/create";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal CustomUserDetails principal,
                          @RequestParam String name,
                          @RequestParam(required = false) String icon,
                          @RequestParam BigDecimal targetAmount,
                          @RequestParam(required = false) LocalDate targetDate) {
        goalService.createGoal(principal.getUser(), name, icon, targetAmount, targetDate);
        return "redirect:/goals";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        SavingsGoal goal = goalService.getById(id);
        model.addAttribute("goal", goal);
        return "goals/view";
    }

    /**
     * Kicks off an STK push so the user confirms the deposit with their M-Pesa PIN.
     * The actual balance credit happens in MpesaCallbackController once Safaricom
     * confirms the payment. In the sandbox, mark as instantly successful for demos.
     */
    @PostMapping("/{id}/deposit")
    public String deposit(@AuthenticationPrincipal CustomUserDetails principal,
                           @PathVariable Long id,
                           @RequestParam BigDecimal amount,
                           Model model) {
        User user = principal.getUser();
        SavingsGoal goal = goalService.getById(id);
        try {
            darajaService.initiateStkPush(user.getPhoneNumber(), amount, "GOAL-" + goal.getId());
            model.addAttribute("message", "Check your phone to enter your M-Pesa PIN and complete the deposit.");
        } catch (Exception ex) {
            log.warn("Daraja STK push failed, falling back to instant sandbox credit: {}", ex.getMessage());
            // Sandbox fallback so the demo/academic flow still works without live Daraja creds
            goalService.depositToGoal(user, goal, amount, "Manual sandbox deposit");
            model.addAttribute("message", "Deposit recorded (sandbox mode).");
        }
        model.addAttribute("goal", goalService.getById(id));
        return "goals/view";
    }
}
