package com.example.demo.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints de autenticación")
class AuthController {

    @PostMapping("/login")
    public String login() {
        return "loged";
    }

    @PostMapping("/register")
    public String register() {
        return "register";
    }
    
}