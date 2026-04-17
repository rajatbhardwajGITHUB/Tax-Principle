package com.example.Usermangement.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:no-reply@example.com}")
    private String fromAddress;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    public boolean sendOtpEmail(String toEmail, String purpose, String otp) {
        if (!mailEnabled || mailSender == null) {
            log.warn("Email delivery is disabled or not configured. OTP email to {} was not sent.", toEmail);
            return false;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject(buildSubject(purpose));
        message.setText(buildBody(purpose, otp));
        mailSender.send(message);
        return true;
    }

    private String buildSubject(String purpose) {
        return switch (purpose) {
            case "signup verification" -> "Verify your email";
            case "login verification" -> "Your login verification code";
            case "password reset" -> "Your password reset code";
            default -> "Your verification code";
        };
    }

    private String buildBody(String purpose, String otp) {
        return "Your " + purpose + " OTP is: " + otp + "\n\n"
                + "This code is valid for a limited time. If you did not request it, you can ignore this email.";
    }
}
