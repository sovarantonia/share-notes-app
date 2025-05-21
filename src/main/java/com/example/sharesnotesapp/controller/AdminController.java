package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.service.ResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Value("${app.resetToken}")
    private String resetToken;

    private final ResetService resetService;

    @Autowired
    public AdminController(ResetService resetService) {
        this.resetService = resetService;
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetData(@RequestHeader("X-Reset-Auth") String token) {
        if (!resetToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid reset token");
        }

        resetService.reset();
        return ResponseEntity.ok("Database has been reset.");
    }
}
