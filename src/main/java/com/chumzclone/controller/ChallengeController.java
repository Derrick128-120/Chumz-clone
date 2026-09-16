package com.chumzclone.controller;

import com.chumzclone.security.CustomUserDetails;
import com.chumzclone.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    public String list(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("challenges", challengeService.getAllChallenges());
        model.addAttribute("myChallenges", challengeService.getUserChallenges(principal.getUser()));
        return "challenges/list";
    }

    @PostMapping("/{id}/enroll")
    public String enroll(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id) {
        challengeService.enroll(principal.getUser(), id);
        return "redirect:/challenges";
    }
}
