package com.erarslan.patient_service.rules;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.erarslan.patient_service.exception.EmailAlreadyExistsException;
import com.erarslan.patient_service.exception.PatientNotFoundByIdException;
import com.erarslan.patient_service.repository.PatientRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PatientBusinessRules {
    private final PatientRepository patientRepository;

    public void checkIfPatientExistsByEmail(String email) {
        boolean exists = patientRepository.existsByEmail(email);
        if (exists) {
            throw new EmailAlreadyExistsException();
        }
    }

    public void checkIfPatientNotExistById(String id) {
        boolean exists = patientRepository.existsById(UUID.fromString(id));
        if (!exists) {
            throw new PatientNotFoundByIdException();
        }
    }
}
