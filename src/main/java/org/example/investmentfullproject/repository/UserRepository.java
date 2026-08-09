package org.example.investmentfullproject.repository;

import org.example.investmentfullproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User,Integer> {

    // Find user by username
    Optional<User> findByUsername(String username);

}