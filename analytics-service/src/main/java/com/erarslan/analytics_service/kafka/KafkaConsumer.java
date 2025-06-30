package com.erarslan.analytics_service.kafka;

import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.protobuf.InvalidProtocolBufferException;

import patient.event.PatientEvent;

@Service
public class KafkaConsumer {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);

            //Simulate processing the event
            logger.info("Received PatientEvent For Consuming: {}", patientEvent);
            logger.info("Processed PatientEvent In Analytics Service: {}", patientEvent.getPatientId());
        } catch (InvalidProtocolBufferException e) {
            logger.error("Failed to parse PatientEvent from byte array", e.getMessage());
        }
    }
}