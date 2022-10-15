package com.deyeva.dossier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ApplicationStatusHistoryDTO {
    private ApplicationStatus status;
    private LocalDateTime time;
    private ApplicationStatus changeType;
}
