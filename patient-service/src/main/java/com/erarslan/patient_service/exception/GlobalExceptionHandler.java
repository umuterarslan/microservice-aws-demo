package com.erarslan.patient_service.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        Map<String, String> error = Map.of("message", ex.getMessage());
        return ResponseEntity.status(409).body(error);
    }

    @ExceptionHandler(PatientNotFoundByIdException.class)
    public ResponseEntity<Map<String, String>> handlePatientNotFoundByIdException(PatientNotFoundByIdException ex) {
        Map<String, String> error = Map.of("message", ex.getMessage());
        return ResponseEntity.status(404).body(error);
    }
}
