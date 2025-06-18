package com.erarslan.patient_service.exception;

public class PatientNotFoundByIdException extends RuntimeException {
    public PatientNotFoundByIdException() {
        super("Patient not found by id");
    }
}
