package com.nttdata.infrastructure.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlingTest {

    private final ErrorHandling errorHandling = new ErrorHandling();

    @Test
    void handleEntityNotFound_ShouldReturnNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("Entidade não encontrada");
        ResponseEntity<String> response = errorHandling.handleEntityNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entidade não encontrada", response.getBody());
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestAndErrors() {
        FieldError fieldError = new FieldError("objectName", "field", "deve ser preenchido");
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<List<String>> response = errorHandling.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(List.of("Campo 'field' deve ser preenchido"), response.getBody());
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerError() {
        RuntimeException ex = new RuntimeException("Erro inesperado");
        ResponseEntity<String> response = errorHandling.handleRuntimeException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro inesperado", response.getBody());
    }

    @Test
    void handleGeneralException_ShouldReturnInternalServerError() {
        Exception ex = new Exception("Erro geral");
        ResponseEntity<String> response = errorHandling.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocorreu um erro inesperado. Tente novamente mais tarde.", response.getBody());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");
        ResponseEntity<String> response = errorHandling.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Argumento inválido", response.getBody());
    }

    @Test
    void handleIllegalStateException_ShouldReturnConflict() {
        IllegalStateException ex = new IllegalStateException("Estado inválido");
        ResponseEntity<String> response = errorHandling.handleIllegalStateException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Estado inválido", response.getBody());
    }
}
