package com.erarslan.patient_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.erarslan.patient_service.exception.PatientNotFoundByIdException;
import com.erarslan.patient_service.mapper.PatientMapper;
import com.erarslan.patient_service.model.dto.CreatePatientRequestDTO;
import com.erarslan.patient_service.model.dto.PatientResponseDTO;
import com.erarslan.patient_service.model.dto.UpdatePatientRequestDto;
import com.erarslan.patient_service.repository.PatientRepository;
import com.erarslan.patient_service.rules.PatientBusinessRules;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final PatientBusinessRules patientBusinessRules;

    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toResponseDTO)
                .toList();
    }

    public PatientResponseDTO getPatientById(String id) {
        return patientRepository.findById(UUID.fromString(id))
                .map(patientMapper::toResponseDTO)
                .orElseThrow(() -> new PatientNotFoundByIdException());
    }

    public PatientResponseDTO createPatient(CreatePatientRequestDTO createPatientRequestDTO) {
        patientBusinessRules.checkIfPatientExistsByEmail(createPatientRequestDTO.getEmail());

        return patientMapper.toResponseDTO(
            patientRepository.save(
                patientMapper.createDtoToEntity(createPatientRequestDTO)
            )
        );
    }

    public PatientResponseDTO updatePatient(UpdatePatientRequestDto updatePatientRequestDto) {
        patientBusinessRules.checkIfPatientNotExistById(updatePatientRequestDto.getId());
        patientBusinessRules.checkIfPatientExistsByEmail(updatePatientRequestDto.getEmail());

        return patientMapper.toResponseDTO(
            patientRepository.save(
                patientMapper.updateDtoToEntity(
                    updatePatientRequestDto, 
                    patientRepository.findById(UUID.fromString(updatePatientRequestDto.getId())).get()
                    )
            )
        );
    }

    public void deletePatient(String id) {
        patientBusinessRules.checkIfPatientNotExistById(id);
        patientRepository.deleteById(UUID.fromString(id));
    }
}
