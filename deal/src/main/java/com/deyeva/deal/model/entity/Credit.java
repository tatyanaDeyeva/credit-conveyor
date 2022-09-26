package com.deyeva.deal.model.entity;

import com.deyeva.deal.model.CreditStatus;
import com.deyeva.deal.model.PaymentScheduleElement;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity(name = "credit")
@Data
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "term")
    private Integer term;
    @Column(name = "monthly_payment")
    private BigDecimal monthly_payment;
    @Column(name = "rate")
    private BigDecimal rate;
    @Column(name = "psk")
    private BigDecimal psk;
    @Column(name = "payment_schedule")
    @Type(type = "jsonb")
    private List<PaymentScheduleElement> payment_schedule;
    @Column(name = "is_insurance_enabled")
    private Boolean is_insurance_enabled = false;
    @Column(name = "is_salary_client")
    private Boolean is_salary_client = false;
    @Column(name = "credit_status")
    @Enumerated(EnumType.STRING)
    private CreditStatus credit_status;
}
