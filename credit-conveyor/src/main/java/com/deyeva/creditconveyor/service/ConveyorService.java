package com.deyeva.creditconveyor.service;

import com.deyeva.creditconveyor.exception.RefusalException;
import com.deyeva.creditconveyor.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConveyorService {
    private final BigDecimal MIN_AMOUNT_FOR_GOOD_RATE = BigDecimal.valueOf(500000);
    private final Integer MIN_TERM_FOR_GOOD_RATE = 24;
    private final BigDecimal BASE_RATE = BigDecimal.valueOf(8);
    private final BigDecimal INSURANCE_AMOUNT = BigDecimal.valueOf(150000);

    public List<LoanOfferDTO> listOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        List<LoanOfferDTO> loanOffers = new ArrayList<>();

        loanOffers.add(createLoanOffer(loanApplicationRequestDTO, false, false));
        loanOffers.add(createLoanOffer(loanApplicationRequestDTO, false, true));
        loanOffers.add(createLoanOffer(loanApplicationRequestDTO, true, false));
        loanOffers.add(createLoanOffer(loanApplicationRequestDTO, true, true));

        return loanOffers;
    }

    public CreditDTO loanParameters(ScoringDataDTO scoringDataDTO) {
        BigDecimal totalAmount = scoringDataDTO.getAmount();
        if (scoringDataDTO.getIsInsuranceEnabled()) {
            totalAmount = totalAmount.add(INSURANCE_AMOUNT);
        }
        BigDecimal rate = evaluateRateForScoring(scoringDataDTO);

        BigDecimal finalMonthlyPayment = evaluateMonthlyPayment(
                totalAmount,
                scoringDataDTO.getTerm(),
                rate);

        return new CreditDTO()
                .amount(scoringDataDTO.getAmount())
                .term(scoringDataDTO.getTerm())
                .monthlyPayment(finalMonthlyPayment)
                .rate(rate)
                .psk(evaluatePsk(scoringDataDTO, finalMonthlyPayment))
                .isInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDTO.getIsSalaryClient())
                .paymentSchedule(evaluatePaymentScheduleElement(scoringDataDTO, rate));
    }

    public LoanOfferDTO createLoanOffer(LoanApplicationRequestDTO loanApplicationRequestDTO, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal totalAmount = loanApplicationRequestDTO.getAmount();
            if (isInsuranceEnabled) {
                totalAmount = totalAmount.add(INSURANCE_AMOUNT);
            }
        BigDecimal rate = evaluateRateForPrescoring(
                isInsuranceEnabled,
                isSalaryClient,
                loanApplicationRequestDTO.getAmount(),
                loanApplicationRequestDTO.getTerm()
        );
        BigDecimal monthlyPayment = evaluateMonthlyPayment(
                totalAmount,
                loanApplicationRequestDTO.getTerm(),
                rate
        );

        return new LoanOfferDTO()
//                .applicationId((int) (Math.random()*Integer.MAX_VALUE))
                .applicationId(1)   //todo replace with actual appId
                .requestedAmount(loanApplicationRequestDTO.getAmount())
                .totalAmount(totalAmount)
                .term(loanApplicationRequestDTO.getTerm())
                .rate(rate)
                .monthlyPayment(monthlyPayment)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient);
    }

    public BigDecimal evaluateRateForPrescoring(Boolean isInsuranceEnabled, Boolean isSalaryClient, BigDecimal requestedAmount, Integer term){
        BigDecimal finalRate = BASE_RATE;
        if (isSalaryClient) {
            finalRate = finalRate.subtract(BigDecimal.valueOf(1));
        }
        if (isInsuranceEnabled) {
            finalRate = finalRate.subtract(BigDecimal.valueOf(3));
        }
        if (requestedAmount.compareTo(MIN_AMOUNT_FOR_GOOD_RATE) < 0) {
            finalRate = finalRate.add(BigDecimal.valueOf(1));
        }
        if (term < MIN_TERM_FOR_GOOD_RATE) {
            finalRate = finalRate.add(BigDecimal.valueOf(0.5));
        }

        return finalRate;
    }

    public BigDecimal evaluateRateForScoring(ScoringDataDTO scoringDataDTO) {
        BigDecimal finalRate = BASE_RATE;
        List<String> refuseNotes = new ArrayList<>();

        switch (scoringDataDTO.getEmployment().getEmploymentStatus()) {
            case UNEMPLOYED: refuseNotes.add("An unemployed person cannot be given a loan");
                break;
            case SELF_EMPLOYED: finalRate = finalRate.add(BigDecimal.ONE);
                break;
            case BUSINESS_OWNER: finalRate = finalRate.add(BigDecimal.valueOf(3));
                break;
        }

        switch (scoringDataDTO.getEmployment().getPosition()) {
            case MID_MANAGER: finalRate = finalRate.subtract(BigDecimal.valueOf(2));
                break;
            case TOP_MANAGER: finalRate = finalRate.subtract(BigDecimal.valueOf(4));
                break;
        }

        if (scoringDataDTO.getEmployment().getSalary().multiply(BigDecimal.valueOf(20)).compareTo(scoringDataDTO.getAmount()) < 0 ) {
            refuseNotes.add("The loan amount cannot be more than 20 salaries");
        }

        if (BigDecimal.valueOf(scoringDataDTO.getDependentAmount()).compareTo(BigDecimal.valueOf(1)) < 0) {
            finalRate = finalRate.add(BigDecimal.valueOf(1));
        }

        switch (scoringDataDTO.getMaritalStatus()) {
            case MARRIED: finalRate = finalRate.subtract(BigDecimal.valueOf(3));
                break;
            case DIVORCED: finalRate = finalRate.add(BigDecimal.valueOf(1));
                break;
        }

        if (20 > ChronoUnit.YEARS.between(scoringDataDTO.getBirthdate(), LocalDate.now()) ||
                ChronoUnit.YEARS.between(scoringDataDTO.getBirthdate(), LocalDate.now()) > 60 ) {
            refuseNotes.add("Age restrictions for issuing a loan");
        }

        switch (scoringDataDTO.getGender()) {
            case NON_BINARY: finalRate = finalRate.add(BigDecimal.valueOf(3));
                break;
            case FEMALE: if (35 < ChronoUnit.YEARS.between(scoringDataDTO.getBirthdate(), LocalDate.now()) &&
                    ChronoUnit.YEARS.between(scoringDataDTO.getBirthdate(), LocalDate.now()) < 60)
                finalRate = finalRate.subtract(BigDecimal.valueOf(3));
                break;
            case MALE: if (30 < ChronoUnit.YEARS.between(scoringDataDTO.getBirthdate(), LocalDate.now()) &&
                    ChronoUnit.YEARS.between(scoringDataDTO.getBirthdate(), LocalDate.now()) < 55)
                finalRate = finalRate.subtract(BigDecimal.valueOf(3));
                break;
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < 12)
            refuseNotes.add("Work experience less than 12 months");

        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < 3)
            refuseNotes.add("Work experience at a new job is less than 12 months");

        if (!refuseNotes.isEmpty()) {
            throw new RefusalException(refuseNotes);
        }

        return finalRate;
    }

    public BigDecimal evaluateMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal rate) {
        rate = (rate.divide(BigDecimal.valueOf(12), 8, RoundingMode.CEILING)).multiply(BigDecimal.valueOf(0.01));
        BigDecimal action = (BigDecimal.ONE.add(rate)).pow(term);
        BigDecimal denominator = action.subtract(BigDecimal.ONE); // низ
        BigDecimal numerator = action.multiply(rate); //вверх
        BigDecimal formula = (totalAmount.multiply(numerator.divide(denominator, 8, RoundingMode.CEILING)));
        return formula.setScale(2, RoundingMode.CEILING);
    }

    public List<PaymentScheduleElement> evaluatePaymentScheduleElement(ScoringDataDTO scoringDataDTO, BigDecimal rate) {
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();

        BigDecimal totalAmount = scoringDataDTO.getAmount();
        if (scoringDataDTO.getIsInsuranceEnabled()) {
            totalAmount = totalAmount.add(INSURANCE_AMOUNT);
        }

        BigDecimal totalPayment = evaluateMonthlyPayment(
                totalAmount,
                scoringDataDTO.getTerm(),
                rate
        );
        rate = rate.multiply(BigDecimal.valueOf(0.01));

        for (int i = 0; i < scoringDataDTO.getTerm(); i++) {
            BigDecimal amount = totalPayment.multiply(BigDecimal.valueOf(scoringDataDTO.getTerm()))
                    .subtract(totalPayment.multiply(BigDecimal.valueOf(i)));
            BigDecimal interestPayment = (amount.multiply(rate)
                    .multiply(BigDecimal.valueOf(ChronoUnit.MONTHS.getDuration().toDays()))
                    .divide(BigDecimal.valueOf(ChronoUnit.YEARS.getDuration().toDays()), 8, RoundingMode.CEILING));
            paymentSchedule.add(new PaymentScheduleElement()
                    .number(i+1)
                    .date(ChronoUnit.MONTHS.addTo(LocalDate.now(), i+1))
                    .totalPayment(totalPayment)
                    .interestPayment(interestPayment.setScale(2, RoundingMode.CEILING))
                    .debtPayment((totalPayment.subtract(interestPayment).setScale(2, RoundingMode.CEILING)))
                    .remainingDebt(amount.subtract(totalPayment))
            );
        }
        return paymentSchedule;
    }

    public BigDecimal evaluatePsk(ScoringDataDTO scoringDataDTO, BigDecimal monthlyPayment) {
        BigDecimal totalAmount = scoringDataDTO.getAmount();
        BigDecimal allPayments = monthlyPayment.multiply(BigDecimal.valueOf(scoringDataDTO.getTerm()));
        if (scoringDataDTO.getIsInsuranceEnabled()) {
            totalAmount = totalAmount.add(INSURANCE_AMOUNT);
            allPayments = allPayments.add(INSURANCE_AMOUNT);
        }
        BigDecimal numerator = (allPayments.divide(totalAmount, 8, RoundingMode.CEILING))
                .subtract(BigDecimal.ONE);
        BigDecimal denominator = BigDecimal.valueOf(scoringDataDTO.getTerm()).divide(BigDecimal.valueOf(12), 2, RoundingMode.CEILING);
        BigDecimal finalPsk = numerator.divide(denominator, 8, RoundingMode.CEILING).multiply(BigDecimal.valueOf(100));
        return finalPsk.setScale(3, RoundingMode.CEILING);
    }
}
