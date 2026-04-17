package com.example.Usermangement.Bean;

import java.time.LocalDateTime;

import com.example.Usermangement.Enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    private String refreshToken;

    @Column(nullable = false)
    private boolean emailVerified = false;

    private String signupOtp;

    private LocalDateTime signupOtpExpiresAt;

    private String loginOtp;

    private LocalDateTime loginOtpExpiresAt;

    private String passwordResetOtp;

    private LocalDateTime passwordResetOtpExpiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getSignupOtp() {
        return signupOtp;
    }

    public void setSignupOtp(String signupOtp) {
        this.signupOtp = signupOtp;
    }

    public LocalDateTime getSignupOtpExpiresAt() {
        return signupOtpExpiresAt;
    }

    public void setSignupOtpExpiresAt(LocalDateTime signupOtpExpiresAt) {
        this.signupOtpExpiresAt = signupOtpExpiresAt;
    }

    public String getLoginOtp() {
        return loginOtp;
    }

    public void setLoginOtp(String loginOtp) {
        this.loginOtp = loginOtp;
    }

    public LocalDateTime getLoginOtpExpiresAt() {
        return loginOtpExpiresAt;
    }

    public void setLoginOtpExpiresAt(LocalDateTime loginOtpExpiresAt) {
        this.loginOtpExpiresAt = loginOtpExpiresAt;
    }

    public String getPasswordResetOtp() {
        return passwordResetOtp;
    }

    public void setPasswordResetOtp(String passwordResetOtp) {
        this.passwordResetOtp = passwordResetOtp;
    }

    public LocalDateTime getPasswordResetOtpExpiresAt() {
        return passwordResetOtpExpiresAt;
    }

    public void setPasswordResetOtpExpiresAt(LocalDateTime passwordResetOtpExpiresAt) {
        this.passwordResetOtpExpiresAt = passwordResetOtpExpiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
