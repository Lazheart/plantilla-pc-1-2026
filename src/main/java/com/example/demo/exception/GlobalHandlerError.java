package com.example.demo.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.demo.user.UserMethodNotAllowed;
import com.example.demo.user.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerError {

    // ==========================================
    // 1. USUARIO NO ENCONTRADO (404 NOT FOUND)
    // ==========================================
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        log.warn("Usuario no encontrado en [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado en [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "El recurso solicitado no fue encontrado", request);
    }

    // ========================================================
    // 2. USUARIO CON PERMISOS INSUFICIENTES (403 FORBIDDEN)
    // ========================================================
    @ExceptionHandler({InsufficientPermissionsException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            Exception ex, HttpServletRequest request) {
        log.warn("Acceso denegado/permisos insuficientes en [{}]: {}", request.getRequestURI(), ex.getMessage());
        String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : "No cuenta con los permisos suficientes para acceder a este recurso";
        return buildResponse(HttpStatus.FORBIDDEN, message, request);
    }

    // ==========================================
    // 3. BAD REQUEST (400 BAD REQUEST)
    // ==========================================
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(
            BadRequestException ex, HttpServletRequest request) {
        log.warn("Bad request de negocio en [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn("Error de validación de argumentos en [{}]: {}", request.getRequestURI(), fieldErrors);
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Error de validación en los campos proporcionados",
                request,
                fieldErrors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Cuerpo de petición no legible en [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud es inválido o no tiene un formato JSON legible",
                request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        String message = String.format("El parámetro '%s' debe ser de tipo %s", ex.getName(), requiredType);
        log.warn("Discrepancia de tipo de parámetro en [{}]: {}", request.getRequestURI(), message);
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Argumento ilegal en [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // ========================================================
    // 4. CREDENCIALES INVÁLIDAS / NO AUTORIZADO (401 UNAUTHORIZED)
    // ========================================================
    @ExceptionHandler({UnauthorizedException.class, BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            Exception ex, HttpServletRequest request) {
        log.warn("No autorizado en [{}]: {}", request.getRequestURI(), ex.getMessage());
        String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : "Credenciales inválidas o sesión no autorizada";
        return buildResponse(HttpStatus.UNAUTHORIZED, message, request);
    }

    // ========================================================
    // 5. MÉTODO HTTP NO PERMITIDO (405 METHOD NOT ALLOWED)
    // ========================================================
    @ExceptionHandler({UserMethodNotAllowed.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(
            Exception ex, HttpServletRequest request) {
        log.warn("Método HTTP no permitido en [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request);
    }

    // ========================================================
    // 6. CONFLICTO / RECURSO DUPLICADO (409 CONFLICT)
    // ========================================================
    @ExceptionHandler({UserAlreadyExistsException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleConflictException(
            Exception ex, HttpServletRequest request) {
        log.warn("Conflicto de recurso en [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // ========================================================
    // 7. ERROR GLOBAL NO CONTROLADO (500 INTERNAL SERVER ERROR)
    // ========================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en el servidor en [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ha ocurrido un error inesperado en el servidor",
                request
        );
    }

    // ========================================================
    // MÉTODOS AUXILIARES
    // ========================================================
    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String message, HttpServletRequest request) {
        return buildResponse(status, message, request, null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String message, HttpServletRequest request, Map<String, String> errors) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .errors(errors)
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }
}
