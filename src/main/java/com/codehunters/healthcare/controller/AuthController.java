package com.codehunters.healthcare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // DEMO-ONLY: Demo login endpoint — must be removed before production
    @PostMapping("/demo-login")
    public String demoLogin() {
        return "redirect:/patient";
    }
}
