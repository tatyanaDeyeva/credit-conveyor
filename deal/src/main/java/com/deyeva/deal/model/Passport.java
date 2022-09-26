package com.deyeva.deal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Passport {
    private String series;
    private String number;
    private LocalDate issueDate;
    private String issueBranch;
}
