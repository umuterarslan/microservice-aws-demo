package com.erarslan.patient_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.erarslan.patient_service.model.dto.CreatePatientRequestDTO;
import com.erarslan.patient_service.model.dto.PatientResponseDTO;
import com.erarslan.patient_service.model.dto.UpdatePatientRequestDto;
import com.erarslan.patient_service.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;



@RestController
@RequestMapping("/patients")
@AllArgsConstructor
@Tag(name = "Patient Management", description = "Operations related to patient management")
public class PatientController {
    private final PatientService patientService;
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all patients", description = "Retrieve a list of all patients")
    public List<PatientResponseDTO> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get patient by ID", description = "Retrieve a patient by their unique ID")
    public PatientResponseDTO getPatientById(@PathVariable String id) {
        return patientService.getPatientById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new patient", description = "Add a new patient to the system")
    public PatientResponseDTO createPatient(@RequestBody @Valid CreatePatientRequestDTO createPatientRequestDTO) {
        return patientService.createPatient(createPatientRequestDTO);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing patient", description = "Modify the details of an existing patient")
    public PatientResponseDTO updatePatient(@RequestBody @Valid UpdatePatientRequestDto updatePatientRequestDto) {
        return patientService.updatePatient(updatePatientRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a patient", description = "Remove a patient from the system by their unique ID")
    public void deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
    }
}
