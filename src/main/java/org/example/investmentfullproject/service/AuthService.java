package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    // Handles user database operations
    @Autowired
    private UserRepository userRepository;

    // Register a new user
    public User registerUser(User user) {

        // Check if username already exists
        if(userRepository
                .findByUsername(user.getUsername())
                .isPresent()) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }

        // Prevent admin self-registration
        if(user.getRole() == Role.ADMIN) {

            throw new RuntimeException(
                    "Admin Registration Not Allowed"
            );
        }

        return userRepository.save(user);
    }

    // Validate user login credentials
    public User login(
            String username,
            String password) {

        // Find user by username
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid Username"));

        // Verify password
        if (!user.getPassword().equals(password)) {

            throw new RuntimeException(
                    "Invalid Password");
        }

        return user;
    }

    // Get user details by ID
    public User getUserProfile(
            Integer id) {

        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User Not Found"));
    }

    // Fetch all users
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    // Delete user by ID
    public void deleteUser(Integer id) {

        userRepository.deleteById(id);
    }

    // Update user profile information
    public User updateProfile(
            User user,
            String currentPassword){

        User existingUser =
                userRepository.findById(
                                user.getUserId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User Not Found"
                                ));

        existingUser.setUsername(
                user.getUsername()
        );

        existingUser.setEmail(
                user.getEmail()
        );

        if(user.getPassword() != null
                &&
                !user.getPassword().isBlank()){

            if(!existingUser.getPassword()
                    .equals(currentPassword)){

                throw new RuntimeException(
                        "Current Password is incorrect"
                );
            }

            existingUser.setPassword(
                    user.getPassword()
            );
        }

        return userRepository.save(
                existingUser
        );
    }
}