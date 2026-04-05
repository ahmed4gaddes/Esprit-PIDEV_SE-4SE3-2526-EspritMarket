package tn.esprit.esprit_market.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User not found");

        ResponseEntity<Map<String, String>> res = handler.handleResourceNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertEquals("User not found", res.getBody().get("error"));
    }

    @Test
    void testHandleUserException() {
        UserException ex = new UserException("Email invalide");

        ResponseEntity<Map<String, String>> res = handler.handleUserException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("Email invalide", res.getBody().get("error"));
    }

    @Test
    void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<Map<String, String>> res = handler.handleHttpMessageNotReadableException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("Invalid input format or invalid role value.", res.getBody().get("error"));
    }

    @Test
    void testHandleValidationExceptions() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("user", "email", "must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, String>> res = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("must not be blank", res.getBody().get("email"));
    }

    @Test
    void testHandleDataIntegrityViolation_DuplicateEmail() {
        Exception rootCause = new Exception("Duplicate entry 'test@mail.com' for key 'email'");
        DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
        when(ex.getMostSpecificCause()).thenReturn(rootCause);

        ResponseEntity<Map<String, String>> res = handler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertEquals("Email already exists.", res.getBody().get("error"));
    }

    @Test
    void testHandleDataIntegrityViolation_DuplicateOther() {
        Exception rootCause = new Exception("Duplicate entry 'value' for key 'name'");
        DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
        when(ex.getMostSpecificCause()).thenReturn(rootCause);

        ResponseEntity<Map<String, String>> res = handler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertEquals("Duplicate value already exists.", res.getBody().get("error"));
    }

    @Test
    void testHandleDataIntegrityViolation_Generic() {
        DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
        when(ex.getMostSpecificCause()).thenReturn(null);
        when(ex.getMessage()).thenReturn("some constraint");

        ResponseEntity<Map<String, String>> res = handler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertEquals("A database constraint was violated.", res.getBody().get("error"));
    }

    @Test
    void testHandleGlobalException() {
        Exception ex = new Exception("Something broke");

        ResponseEntity<Map<String, String>> res = handler.handleGlobalException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
        assertTrue(res.getBody().get("error").contains("Something broke"));
    }
}
