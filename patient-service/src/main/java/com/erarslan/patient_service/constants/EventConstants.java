package com.erarslan.patient_service.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventConstants {
    PATIENT_CREATED("PATIENT_CREATED");

    private final String eventType;
}
