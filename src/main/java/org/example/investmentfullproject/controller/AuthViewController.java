package org.example.investmentfullproject.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Handles login, registration and profile actions
@Controller
public class AuthViewController {

    private final AuthService authService;

    public AuthViewController(AuthService authService) {
        this.authService = authService;
    }

    // Process user login
    @PostMapping("/login")
    public String login(
            User user,
            HttpSession session,
            Model model) {

        try {
            User loggedUser = authService.login(
                    user.getUsername(),
                    user.getPassword());

            if (loggedUser.getRole() != user.getRole()) {

                model.addAttribute("user", user);
                model.addAttribute(
                        "error",
                        "Selected role does not match account role");

                return "login";
            }

            session.setAttribute(
                    "loggedUser",
                    loggedUser);

            return switch (loggedUser.getRole()) {

                case ADMIN ->
                        "redirect:/admin";

                case ADVISOR ->
                        "redirect:/advisor";

                case INVESTOR ->
                        "redirect:/investor";
            };

        } catch (RuntimeException exception) {

            model.addAttribute("user", user);
            model.addAttribute(
                    "error",
                    exception.getMessage());

            return "login";
        }
    }

    // Process user registration
    @PostMapping("/register")
    public String register(
            @Valid User user,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("user", user);

            return "register";
        }

        try {

            authService.registerUser(user);

            return "redirect:/?registered=true";

        } catch (RuntimeException exception) {

            model.addAttribute("user", user);

            model.addAttribute(
                    "error",
                    exception.getMessage());

            return "register";
        }
    }


    // Update user profile
    @PostMapping("/profile/update")
    public String updateProfile(
            User submittedUser,
            @RequestParam(required = false)
            String currentPassword,
            HttpSession session,
            Model model) {

        User loggedUser =
                (User) session.getAttribute(
                        "loggedUser");

        if (loggedUser == null) {
            return "redirect:/";
        }

        submittedUser.setUserId(
                loggedUser.getUserId());

        submittedUser.setRole(
                loggedUser.getRole());

        try {

            User updatedUser =
                    authService.updateProfile(
                            submittedUser,
                            currentPassword);

            session.setAttribute(
                    "loggedUser",
                    updatedUser);

            model.addAttribute(
                    "user",
                    updatedUser);

            model.addAttribute(
                    "success",
                    "Profile updated successfully.");

        } catch (RuntimeException exception) {

            submittedUser.setPassword(null);

            model.addAttribute(
                    "user",
                    submittedUser);

            model.addAttribute(
                    "error",
                    exception.getMessage());
        }

        return "profile";
    }
}