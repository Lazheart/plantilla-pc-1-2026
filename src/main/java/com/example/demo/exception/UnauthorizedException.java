package com.example.demo.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("No autorizado o credenciales inválidas");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
