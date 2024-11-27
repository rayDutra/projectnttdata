package com.nttdata.infrastructure.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandling {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException exception) {
        String message = "A entidade solicitada não foi encontrada.";
        return ResponseEntity.status(404).body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationException(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getFieldErrors().stream()
            .map(this::formatErrorMessage)
            .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    private String formatErrorMessage(FieldError error) {
        return String.format("Campo '%s' %s", error.getField(), error.getDefaultMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException exception) {
        String message = exception.getMessage() != null ? exception.getMessage() : "Ocorreu um erro inesperado.";
        return ResponseEntity.status(500).body(message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception exception) {
        String message = "Ocorreu um erro inesperado. Tente novamente mais tarde.";
        return ResponseEntity.status(500).body(message);
    }
}
