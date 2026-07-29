package org.example.investmentfullproject.service;

import org.example.investmentfullproject.model.User;
import org.example.investmentfullproject.model.Role;
import org.example.investmentfullproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {

        if(userRepository
                .findByUsername(user.getUsername())
                .isPresent()) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }

        if(user.getRole() == Role.ADMIN) {

            throw new RuntimeException(
                    "Admin Registration Not Allowed"
            );
        }

        return userRepository.save(user);
    }

    public User login(
            String username,
            String password) {

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid Username"));

        if (!user.getPassword().equals(password)) {

            throw new RuntimeException(
                    "Invalid Password");
        }

        return user;
    }

    public User getUserProfile(
            Integer id) {

        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User Not Found"));
    }
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public void deleteUser(Integer id) {

        userRepository.deleteById(id);
    }
    public Long getUserCount() {

        return userRepository.count();
    }
    public User updateProfile(
            User user,
            String currentPassword){

        System.out.println(
                "Received Password: "
                        + user.getPassword()
        );

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