package com.lostfound.controller;

import com.lostfound.model.User;
import com.lostfound.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ── Login ─────────────────────────────────────────────────
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null)  model.addAttribute("errorMsg",  "Invalid email or password. Please try again.");
        if (logout != null) model.addAttribute("logoutMsg", "You have been signed out successfully.");
        return "login";
    }

    // ── Register GET ──────────────────────────────────────────
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // ── Register POST ─────────────────────────────────────────
    @PostMapping("/register")
    public String register(
            @RequestParam @NotBlank String name,
            @RequestParam @Email   String email,
            @RequestParam @Size(min = 6) String password,
            @RequestParam String confirmPassword,
            @RequestParam(required = false) String phone,
            RedirectAttributes redirectAttributes) {

        // Password match check
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMsg", "Passwords do not match.");
            return "redirect:/register";
        }

        try {
            userService.registerUser(name, email, password, phone);
            redirectAttributes.addFlashAttribute("successMsg",
                "Account created successfully! Please sign in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/register";
        }
    }

}
