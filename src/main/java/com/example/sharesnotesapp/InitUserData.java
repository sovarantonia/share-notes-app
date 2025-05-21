package com.example.sharesnotesapp;

import com.example.sharesnotesapp.repository.*;
import com.example.sharesnotesapp.service.ResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class InitUserData implements CommandLineRunner {

    private final ResetService resetService;


    @Autowired
    public InitUserData(ResetService resetService) {
        this.resetService = resetService;

    }

    @Override
    public void run(String... args) throws Exception {
        resetService.reset();
    }
}
