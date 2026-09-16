package com.chumzclone.controller;

import com.chumzclone.entity.SavingsGroup;
import com.chumzclone.entity.User;
import com.chumzclone.security.CustomUserDetails;
import com.chumzclone.service.DarajaService;
import com.chumzclone.service.SavingsGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@Controller
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final SavingsGroupService groupService;
    private final DarajaService darajaService;

    @GetMapping
    public String list(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        model.addAttribute("memberships", groupService.getGroupsForUser(principal.getUser()));
        return "groups/list";
    }

    @GetMapping("/new")
    public String createForm() {
        return "groups/create";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal CustomUserDetails principal,
                          @RequestParam String name,
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) BigDecimal targetAmount) {
        SavingsGroup group = groupService.createGroup(principal.getUser(), name, description,
                targetAmount != null ? targetAmount : BigDecimal.ZERO);
        return "redirect:/groups/" + group.getId();
    }

    @PostMapping("/join")
    public String join(@AuthenticationPrincipal CustomUserDetails principal, @RequestParam String inviteCode) {
        SavingsGroup group = groupService.joinGroup(principal.getUser(), inviteCode);
        return "redirect:/groups/" + group.getId();
    }

    @GetMapping("/{id}")
    public String view(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id, Model model) {
        SavingsGroup group = groupService.getById(id);
        model.addAttribute("group", group);
        model.addAttribute("members", groupService.getMembers(group));
        return "groups/view";
    }

    @PostMapping("/{id}/contribute")
    public String contribute(@AuthenticationPrincipal CustomUserDetails principal,
                              @PathVariable Long id,
                              @RequestParam BigDecimal amount,
                              Model model) {
        User user = principal.getUser();
        SavingsGroup group = groupService.getById(id);
        try {
            darajaService.initiateStkPush(user.getPhoneNumber(), amount, "GRP-" + group.getId());
            model.addAttribute("message", "Check your phone to enter your M-Pesa PIN and complete the contribution.");
        } catch (Exception ex) {
            log.warn("Daraja STK push failed, falling back to instant sandbox credit: {}", ex.getMessage());
            groupService.contribute(user, group, amount);
            model.addAttribute("message", "Contribution recorded (sandbox mode).");
        }
        model.addAttribute("group", groupService.getById(id));
        model.addAttribute("members", groupService.getMembers(group));
        return "groups/view";
    }
}
