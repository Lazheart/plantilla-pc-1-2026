package com.example.demo.user;

public class UserMethodNotAllowed extends RuntimeException {

    public UserMethodNotAllowed() {
        super("Método HTTP no permitido para esta operación");
    }

    public UserMethodNotAllowed(String message) {
        super(message);
    }
}
