package com.deyeva.deal.model.entity;

import com.deyeva.deal.model.ApplicationStatus;
import com.deyeva.deal.model.ApplicationStatusHistoryDTO;
import com.deyeva.deal.model.LoanOfferDTO;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "application")
@Data
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @OneToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "applied_offer")
    @Type(type = "jsonb")
    private LoanOfferDTO appliedOffer;
    @Column(name = "sign_date")
    private LocalDate signDate;
    @Column(name = "ses_code")
    private String sesCode;
    @Column(name = "status_history")
    @Type(type = "jsonb")
    private List<ApplicationStatusHistoryDTO> statusHistory;
}
