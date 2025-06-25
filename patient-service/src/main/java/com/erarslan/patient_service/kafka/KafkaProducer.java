package com.erarslan.patient_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.erarslan.patient_service.constants.EventConstants;
import com.erarslan.patient_service.grpc.BillingServiceGrpcClient;
import com.erarslan.patient_service.model.entity.Patient;

import lombok.AllArgsConstructor;
import patient.event.PatientEvent;

@Service
@AllArgsConstructor
public class KafkaProducer {

    Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void sendEvent(Patient patient) {
        PatientEvent patientEvent = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType(EventConstants.PATIENT_CREATED.getEventType())
                .build();

        try{
            kafkaTemplate.send("patient", patientEvent.toByteArray());
        } catch (Exception e) {
            logger.error("Error sending {} to Kafka: {}", EventConstants.PATIENT_CREATED.getEventType(), e.getMessage());
        }
    }
}
