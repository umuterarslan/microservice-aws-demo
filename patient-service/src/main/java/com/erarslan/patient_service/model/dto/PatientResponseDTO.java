package com.erarslan.patient_service.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponseDTO {
    private String id;
    private String name;
    private String email;
    private String address;
    private LocalDate dateOfBirth;
}
