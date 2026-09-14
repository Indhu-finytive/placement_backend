package com.uniq.placement.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CandidateStatus {
    REGISTERED("Registered"),
    TRAINING("Training"),
    INTERVIEW_READY("Interview Ready"),
    ATTENDING_INTERVIEWS("Attending Interviews"),
    PLACED("Placed"),
    COLLECTION_RUNNING("Collection Running"),
    FULLY_PAID("Fully Paid"),
    CLOSED("Closed");

    private final String value;

    CandidateStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static CandidateStatus fromValue(String value) {
        for (CandidateStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
            if (s.name().equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown CandidateStatus: " + value);
    }
}
