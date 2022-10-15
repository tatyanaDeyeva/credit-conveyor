package com.deyeva.dossier.model.entity;

import com.deyeva.dossier.model.ApplicationStatus;
import com.deyeva.dossier.model.ApplicationStatusHistoryDTO;
import com.deyeva.dossier.model.LoanOfferDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Application {
    private Long id;
    private Client client;
    private Credit credit;
    private ApplicationStatus status;
    private LocalDateTime creationDate;
    private LoanOfferDTO appliedOffer;
    private LocalDate signDate;
    private String sesCode;
    private List<ApplicationStatusHistoryDTO> statusHistory;
}
