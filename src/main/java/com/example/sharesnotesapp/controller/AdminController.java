package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.service.ResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ResetService resetService;

    @Autowired
    public AdminController(ResetService resetService) {
        this.resetService = resetService;
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetData() {
        resetService.deleteAllData();
        resetService.resetForTesting();
        return ResponseEntity.ok("Database has been reset.");
    }
}
