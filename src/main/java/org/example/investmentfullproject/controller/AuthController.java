package org.example.investmentfullproject.controller;

import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User registerUser(
            @RequestBody User user) {

        return authService.registerUser(user);
    }

    @PostMapping("/login")
    public User login(
            @RequestBody User user) {

        return authService.login(
                user.getUsername(),
                user.getPassword()
        );
    }

    @GetMapping("/profile/{id}")
    public User getUserProfile(
            @PathVariable Integer id) {

        return authService.getUserProfile(id);
    }
    @GetMapping("/users")
    public List<User> getAllUsers() {

        return authService.getAllUsers();
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(
            @PathVariable Integer id) {

        authService.deleteUser(id);

        return "User Deleted Successfully";
    }
    @GetMapping("/count")
    public Long getUserCount() {

        return authService.getUserCount();
    }
    @PutMapping("/profile/update")
    public User updateProfile(
            @RequestBody User user){

        return authService.updateProfile(user);
    }
}