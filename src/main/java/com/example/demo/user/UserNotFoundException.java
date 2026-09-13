package com.example.demo.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long id) {
        super("Usuario no encontrado con ID: " + id);
    }
}
