package com.erarslan.patient_service.service;

import com.erarslan.patient_service.exception.PatientNotFoundByIdException;
import com.erarslan.patient_service.grpc.BillingServiceGrpcClient;
import com.erarslan.patient_service.kafka.KafkaProducer;
import com.erarslan.patient_service.mapper.PatientMapper;
import com.erarslan.patient_service.model.dto.CreatePatientRequestDTO;
import com.erarslan.patient_service.model.dto.PatientResponseDTO;
import com.erarslan.patient_service.model.dto.UpdatePatientRequestDto;
import com.erarslan.patient_service.model.entity.Patient;
import com.erarslan.patient_service.repository.PatientRepository;
import com.erarslan.patient_service.rules.PatientBusinessRules;

import billing.BillingResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private PatientBusinessRules patientBusinessRules;

    @Mock
    private BillingServiceGrpcClient billingServiceGrpcClient;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllPatients_ReturnsListOfPatientResponseDTOs() {
        // Arrange
        Patient patient1 = new Patient();
        Patient patient2 = new Patient();
        List<Patient> patients = Arrays.asList(patient1, patient2);

        PatientResponseDTO responseDTO1 = new PatientResponseDTO();
        PatientResponseDTO responseDTO2 = new PatientResponseDTO();

        when(patientRepository.findAll()).thenReturn(patients);
        when(patientMapper.toResponseDTO(patient1)).thenReturn(responseDTO1);
        when(patientMapper.toResponseDTO(patient2)).thenReturn(responseDTO2);

        // Act
        List<PatientResponseDTO> result = patientService.getAllPatients();

        // Assert
        assertEquals(2, result.size());
        assertEquals(responseDTO1, result.get(0));
        assertEquals(responseDTO2, result.get(1));

        verify(patientRepository, times(1)).findAll();
        verify(patientMapper, times(1)).toResponseDTO(patient1);
        verify(patientMapper, times(1)).toResponseDTO(patient2);
    }

    @Test
    void getPatientById_ExistingId_ReturnsPatientResponseDTO() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        Patient patient = new Patient();
        PatientResponseDTO responseDTO = new PatientResponseDTO();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDTO(patient)).thenReturn(responseDTO);

        // Act
        PatientResponseDTO result = patientService.getPatientById(patientId.toString());

        // Assert
        assertEquals(responseDTO, result);

        verify(patientRepository, times(1)).findById(patientId);
        verify(patientMapper, times(1)).toResponseDTO(patient);
    }

    @Test
    void getPatientById_NonExistingId_ThrowsPatientNotFoundByIdException() {
        // Arrange
        UUID patientId = UUID.randomUUID();

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PatientNotFoundByIdException.class, () -> patientService.getPatientById(patientId.toString()));

        verify(patientRepository, times(1)).findById(patientId);
        verify(patientMapper, never()).toResponseDTO(any());
    }

    @Test
    void createPatient_ValidRequest_ReturnsPatientResponseDTO() {
        // Arrange
        CreatePatientRequestDTO createPatientRequestDTO = new CreatePatientRequestDTO(
                "Test Name",
                "test@email.com",
                "Test Address",
                "01/01/1990"
        );

        Patient patient = new Patient();
        Patient savedPatient = new Patient();
        savedPatient.setId(UUID.randomUUID());

        PatientResponseDTO responseDTO = new PatientResponseDTO();
        responseDTO.setId(UUID.randomUUID().toString());

        BillingResponse billingResponse = BillingResponse.newBuilder()
            .setAccountId("mock-id")
            .setStatus("CREATED")
            .build();

        when(patientMapper.createDtoToEntity(createPatientRequestDTO)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(savedPatient);
        when(patientMapper.toResponseDTO(savedPatient)).thenReturn(responseDTO);
        when(billingServiceGrpcClient.createBillingAccount(anyString(), anyString(), anyString()))
            .thenReturn(billingResponse);
        doNothing().when(kafkaProducer).sendEvent(any(Patient.class));

        // Act
        PatientResponseDTO result = patientService.createPatient(createPatientRequestDTO);

        // Assert
        assertEquals(responseDTO, result);

        verify(patientBusinessRules, times(1)).checkIfPatientExistsByEmail(createPatientRequestDTO.getEmail());
        verify(patientMapper, times(1)).createDtoToEntity(createPatientRequestDTO);
        verify(patientRepository, times(1)).save(patient);
        verify(patientMapper, times(1)).toResponseDTO(savedPatient);
    }

    @Test
    void updatePatient_ValidRequest_ReturnsPatientResponseDTO() {
        // Arrange
        UpdatePatientRequestDto updatePatientRequestDto = new UpdatePatientRequestDto(
                UUID.randomUUID().toString(),
                "Test Name",
                "test@email.com",
                "Test Address",
                "01/01/1990"
        );
        UUID patientId = UUID.fromString(updatePatientRequestDto.getId());
        Patient existingPatient = new Patient();
        Patient updatedPatient = new Patient();
        PatientResponseDTO responseDTO = new PatientResponseDTO();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(patientMapper.updateDtoToEntity(updatePatientRequestDto, existingPatient)).thenReturn(existingPatient);
        when(patientRepository.save(existingPatient)).thenReturn(updatedPatient);
        when(patientMapper.toResponseDTO(updatedPatient)).thenReturn(responseDTO);

        // Act
        PatientResponseDTO result = patientService.updatePatient(updatePatientRequestDto);

        // Assert
        assertEquals(responseDTO, result);

        verify(patientBusinessRules, times(1)).checkIfPatientNotExistById(updatePatientRequestDto.getId());
        verify(patientBusinessRules, times(1)).checkIfPatientExistsByEmail(updatePatientRequestDto.getEmail());
        verify(patientRepository, times(1)).findById(patientId);
        verify(patientMapper, times(1)).updateDtoToEntity(updatePatientRequestDto, existingPatient);
        verify(patientRepository, times(1)).save(existingPatient);
        verify(patientMapper, times(1)).toResponseDTO(updatedPatient);
    }

    @Test
    void deletePatient_ExistingId_DeletesPatient() {
        // Arrange
        String patientId = UUID.randomUUID().toString();

        // Act
        patientService.deletePatient(patientId);

        // Assert
        verify(patientBusinessRules, times(1)).checkIfPatientNotExistById(patientId);
        verify(patientRepository, times(1)).deleteById(UUID.fromString(patientId));
    }
}
