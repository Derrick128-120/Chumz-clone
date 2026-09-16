package com.chumzclone.controller;

import com.chumzclone.entity.User;
import com.chumzclone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/")
    public String root() {
        return "welcome";
    }

    @GetMapping("/auth/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String register(@ModelAttribute RegisterRequest req, Model model) {
        try {
            userService.register(req.getFullName(), req.getEmail(), req.getPhoneNumber(), req.getPassword());
            return "redirect:/auth/login?registered=true";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("registerRequest", req);
            return "auth/register";
        }
    }

    public static class RegisterRequest {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String password;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
