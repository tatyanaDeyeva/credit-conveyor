package com.deyeva.dossier.model.entity;

import com.deyeva.dossier.model.CreditStatus;
import com.deyeva.dossier.model.PaymentScheduleElement;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Credit {
    private Long id;
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private List<PaymentScheduleElement> paymentSchedule;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
    private CreditStatus creditStatus;
}
