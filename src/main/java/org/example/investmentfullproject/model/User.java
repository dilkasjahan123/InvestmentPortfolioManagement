package org.example.investmentfullproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "user")
public class User {

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Integer userId;

    // Unique username
    @NotBlank(message = "Username is required")
    @Size(
            min = 3,
            max = 20,
            message = "Username must be between 3 and 20 characters"
    )
    @Column(unique = true)
    private String username;

    // Password
    @NotBlank(message = "Password is required")
    @Size(
            min = 6,
            message = "Password must be at least 6 characters long"
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // Email
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    // User role (ADMIN, ADVISOR, INVESTOR)
    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}