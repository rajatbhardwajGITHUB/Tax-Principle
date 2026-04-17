package com.example.Usermangement.Model;

import java.time.LocalDateTime;

public class OtpInitiationResponse {

    private String message;
    private String email;
    private LocalDateTime expiresAt;
    private String otp;

    public OtpInitiationResponse() {
    }

    public OtpInitiationResponse(String message, String email, LocalDateTime expiresAt, String otp) {
        this.message = message;
        this.email = email;
        this.expiresAt = expiresAt;
        this.otp = otp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
