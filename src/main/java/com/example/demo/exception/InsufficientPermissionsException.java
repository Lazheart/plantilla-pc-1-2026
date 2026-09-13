package com.example.demo.exception;

public class InsufficientPermissionsException extends RuntimeException {

    public InsufficientPermissionsException() {
        super("El usuario no cuenta con los permisos suficientes para realizar esta acción");
    }

    public InsufficientPermissionsException(String message) {
        super(message);
    }
}
