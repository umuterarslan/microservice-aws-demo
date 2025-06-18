package com.erarslan.patient_service.mapper;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.erarslan.patient_service.model.dto.CreatePatientRequestDTO;
import com.erarslan.patient_service.model.dto.PatientResponseDTO;
import com.erarslan.patient_service.model.dto.UpdatePatientRequestDto;
import com.erarslan.patient_service.model.entity.Patient;

@Component
public class PatientMapper {
    public PatientResponseDTO toResponseDTO(Patient patient) {
        if (patient == null) {
            return null;
        }
        return new PatientResponseDTO(
                patient.getId().toString(),
                patient.getName(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getDateOfBirth());
    }

    public Patient createDtoToEntity(CreatePatientRequestDTO createPatientRequestDTO) {
        if (createPatientRequestDTO == null) {
            return null;
        }
        Patient patient = new Patient();
        patient.setName(createPatientRequestDTO.getName());
        patient.setEmail(createPatientRequestDTO.getEmail());
        patient.setAddress(createPatientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(createPatientRequestDTO.getDateOfBirth()));
        return patient;
    }

    public Patient updateDtoToEntity(UpdatePatientRequestDto updatePatientRequestDto, Patient patient) {
        if (updatePatientRequestDto == null) {
            return null;
        }
        patient.setId(UUID.fromString(updatePatientRequestDto.getId()));
        patient.setName(updatePatientRequestDto.getName());
        patient.setEmail(updatePatientRequestDto.getEmail());
        patient.setAddress(updatePatientRequestDto.getAddress());
        patient.setDateOfBirth(LocalDate.parse(updatePatientRequestDto.getDateOfBirth()));
        return patient;
    }
}
