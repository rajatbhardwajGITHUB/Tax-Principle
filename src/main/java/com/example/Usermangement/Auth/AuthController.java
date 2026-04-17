package com.example.Usermangement.Auth;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import com.example.Usermangement.Model.AuthResponse;
import com.example.Usermangement.Model.ForgotPasswordRequest;
import com.example.Usermangement.Model.LoginRequest;
import com.example.Usermangement.Model.MessageResponse;
import com.example.Usermangement.Model.OtpInitiationResponse;
import com.example.Usermangement.Model.OtpVerificationRequest;
import com.example.Usermangement.Model.RefreshRequest;
import com.example.Usermangement.Model.RegisterRequest;
import com.example.Usermangement.Model.ResetPasswordRequest;

import jakarta.validation.Valid;


@RestController
@Profile("!test")
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register/initiate")
    public ResponseEntity<OtpInitiationResponse> registerInitiate(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerInitiate(request));
    }

    @PostMapping("/register/verify")
    public ResponseEntity<AuthResponse> registerVerify(@Valid @RequestBody OtpVerificationRequest request) {
        return ResponseEntity.ok(authService.verifyRegisterOtp(request));
    }

    @PostMapping("/login/initiate")
    public ResponseEntity<OtpInitiationResponse> loginInitiate(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.loginInitiate(request));
    }

    @PostMapping("/login/verify")
    public ResponseEntity<AuthResponse> loginVerify(@Valid @RequestBody OtpVerificationRequest request){
        return ResponseEntity.ok(authService.verifyLoginOtp(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request){
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        authService.logout(authentication.getName());
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<OtpInitiationResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
    
    
}
