package com.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("mensaje", mensaje);
        return error;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatus(InvalidStatusException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> error = construirRespuesta(HttpStatus.BAD_REQUEST, "Error de validacion");
        Map<String, String> detalles = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(f -> detalles.put(f.getField(), f.getDefaultMessage()));
        error.put("detalles", detalles);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }
}