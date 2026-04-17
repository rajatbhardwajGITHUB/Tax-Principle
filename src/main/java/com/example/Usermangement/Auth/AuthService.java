package com.example.Usermangement.Auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.Usermangement.Bean.User;
import com.example.Usermangement.Enums.Role;
import com.example.Usermangement.Exceptions.AuthException;
import com.example.Usermangement.Exceptions.EmailAlreadyRegisteredException;
import com.example.Usermangement.Exceptions.InvalidCredentialsException;
import com.example.Usermangement.Exceptions.InvalidOtpException;
import com.example.Usermangement.Exceptions.InvalidRefreshTokenException;
import com.example.Usermangement.Exceptions.OtpExpiredException;
import com.example.Usermangement.Exceptions.PhoneNumberAlreadyRegisteredException;
import com.example.Usermangement.Exceptions.UserNotFoundException;
import com.example.Usermangement.Model.AuthResponse;
import com.example.Usermangement.Model.ForgotPasswordRequest;
import com.example.Usermangement.Model.LoginRequest;
import com.example.Usermangement.Model.MessageResponse;
import com.example.Usermangement.Model.OtpInitiationResponse;
import com.example.Usermangement.Model.OtpVerificationRequest;
import com.example.Usermangement.Model.RefreshRequest;
import com.example.Usermangement.Model.RegisterRequest;
import com.example.Usermangement.Model.ResetPasswordRequest;
import com.example.Usermangement.Repository.UserRepository;
import com.example.Usermangement.Service.EmailService;
import com.example.Usermangement.Security.CustomUserDetails;
import com.example.Usermangement.Security.JwtService;

@Service
@Profile("!test")
public class AuthService {

    private static final SecureRandom OTP_RANDOM = new SecureRandom();
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Value("${app.otp.expiration-minutes:10}")
    private long otpExpirationMinutes;

    @Value("${app.otp.return-in-response:true}")
    private boolean returnOtpInResponse;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public OtpInitiationResponse registerInitiate(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        Optional<User> userWithPhone = userRepository.findByPhoneNumber(request.getPhoneNumber());

        if (userWithPhone.isPresent() && existingUser.map(User::getId).map(id -> !id.equals(userWithPhone.get().getId())).orElse(true)) {
            throw new PhoneNumberAlreadyRegisteredException("Phone number already registered");
        }

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            if (user.isEmailVerified()) {
                throw new EmailAlreadyRegisteredException("Email already registered");
            }
        } else {
            user = new User();
            user.setEmail(request.getEmail());
        }

        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole() == null ? Role.USER : request.getRole());
        user.setEmailVerified(false);

        String otp = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpirationMinutes);
        user.setSignupOtp(otp);
        user.setSignupOtpExpiresAt(expiresAt);
        user.setRefreshToken(null);

        userRepository.save(user);
        emailService.sendOtpEmail(request.getEmail(), "signup verification", otp);
        logOtp("signup verification", request.getEmail(), otp, expiresAt);

        return buildOtpResponse("OTP sent for signup verification email", request.getEmail(), expiresAt, otp);
    }

    public AuthResponse verifyRegisterOtp(OtpVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            throw new AuthException("User is already verified. Please login.");
        }

        validateOtp(user.getSignupOtp(), user.getSignupOtpExpiresAt(), request.getOtp());

        user.setEmailVerified(true);
        user.setSignupOtp(null);
        user.setSignupOtpExpiresAt(null);

        return issueTokensForUser(user);
    }

    public OtpInitiationResponse loginInitiate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!user.isEmailVerified()) {
            throw new AuthException("Please complete signup OTP verification before login");
        }

        String otp = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpirationMinutes);
        user.setLoginOtp(otp);
        user.setLoginOtpExpiresAt(expiresAt);
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), "login verification", otp);
        logOtp("login verification", user.getEmail(), otp, expiresAt);

        return buildOtpResponse("OTP sent for login verification email", user.getEmail(), expiresAt, otp);
    }

    public AuthResponse verifyLoginOtp(OtpVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!user.isEmailVerified()) {
            throw new AuthException("Please complete signup verification first");
        }

        validateOtp(user.getLoginOtp(), user.getLoginOtpExpiresAt(), request.getOtp());

        user.setLoginOtp(null);
        user.setLoginOtpExpiresAt(null);

        return issueTokensForUser(user);
    }

    public OtpInitiationResponse forgotPassword(ForgotPasswordRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            return new OtpInitiationResponse(
                    "If this email exists, an OTP has been sent for password reset",
                    request.getEmail(),
                    null,
                    null);
        }

        User user = optionalUser.get();
        String otp = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpirationMinutes);

        user.setPasswordResetOtp(otp);
        user.setPasswordResetOtpExpiresAt(expiresAt);
        userRepository.save(user);
        emailService.sendOtpEmail(request.getEmail(), "password reset", otp);
        logOtp("password reset", request.getEmail(), otp, expiresAt);

        return buildOtpResponse("OTP sent for password reset email", request.getEmail(), expiresAt, otp);
    }

    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        validateOtp(user.getPasswordResetOtp(), user.getPasswordResetOtpExpiresAt(), request.getOtp());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetOtp(null);
        user.setPasswordResetOtpExpiresAt(null);
        user.setRefreshToken(null);
        userRepository.save(user);

        return new MessageResponse("Password reset successful. Please login again.");
    }

    public AuthResponse refresh(RefreshRequest request) {
        String token = request.getRefreshToken();
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserDetails userDetails = new CustomUserDetails(user);

        if (!jwtService.isTokenValid(token, userDetails)) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(token)) {
            throw new InvalidRefreshTokenException("Refresh token not recognized");
        }

        Map<String, Object> claims = buildRoleClaims(user.getRole().name());
        String newAccessToken = jwtService.generateAccessToken(userDetails, claims);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails, claims);
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);
        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    private AuthResponse issueTokensForUser(User user) {
        UserDetails userDetails = new CustomUserDetails(user);
        Map<String, Object> claims = buildRoleClaims(user.getRole().name());
        String accessToken = jwtService.generateAccessToken(userDetails, claims);
        String refreshToken = jwtService.generateRefreshToken(userDetails, claims);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    private OtpInitiationResponse buildOtpResponse(String message, String email, LocalDateTime expiresAt, String otp) {
        return new OtpInitiationResponse(
                message,
                email,
                expiresAt,
                returnOtpInResponse ? otp : null);
    }

    private void validateOtp(String storedOtp, LocalDateTime expiresAt, String providedOtp) {
        if (storedOtp == null || expiresAt == null) {
            throw new InvalidOtpException("OTP not generated. Please request a new OTP.");
        }

        if (LocalDateTime.now().isAfter(expiresAt)) {
            throw new OtpExpiredException("OTP expired. Please request a new OTP.");
        }

        if (!storedOtp.equals(providedOtp)) {
            throw new InvalidOtpException("Invalid OTP");
        }
    }

    private String generateOtp() {
        int otp = OTP_RANDOM.nextInt(1_000_000);
        return String.format("%06d", otp);
    }

    private void logOtp(String purpose, String email, String otp, LocalDateTime expiresAt) {
        log.info("DEV OTP for {} on email {} is {} and expires at {}", purpose, email, otp, expiresAt);
    }

    private Map<String, Object> buildRoleClaims(String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return claims;
    }
}
