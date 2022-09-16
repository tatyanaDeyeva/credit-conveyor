package com.deyeva.creditconveyor.controller;

import com.deyeva.creditconveyor.model.*;
import com.deyeva.creditconveyor.service.ConveyorService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ConveyorControllerTest extends AbstractControllerTest {

    @MockBean
    private ConveyorService conveyorService;

    @Test
    void listOfPossibleLoanOffers() throws Exception {
        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO()
                .amount(BigDecimal.valueOf(3000000))
                .term(12)
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .email("ivanov_86@gmail.com")
                .birthdate(LocalDate.parse("2022-09-10"))
                .passportSeries("3619")
                .passportNumber("725693");

        List<LoanOfferDTO> loanOffers = new ArrayList<>();

        LoanOfferDTO firstOffer = new LoanOfferDTO()
                .applicationId(1)
                .requestedAmount(BigDecimal.valueOf(3000000))
                .totalAmount(BigDecimal.valueOf(3000000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(261659.37))
                .rate(BigDecimal.valueOf(8.5))
                .isInsuranceEnabled(false)
                .isSalaryClient(false);

        LoanOfferDTO secondOffer = new LoanOfferDTO()
                .applicationId(1)
                .requestedAmount(BigDecimal.valueOf(3000000))
                .totalAmount(BigDecimal.valueOf(3000000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(260272.26))
                .rate(BigDecimal.valueOf(7.5))
                .isInsuranceEnabled(false)
                .isSalaryClient(true);

        LoanOfferDTO thirdOffer = new LoanOfferDTO()
                .applicationId(1)
                .requestedAmount(BigDecimal.valueOf(3000000))
                .totalAmount(BigDecimal.valueOf(3150000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(270385.89))
                .rate(BigDecimal.valueOf(5.5))
                .isInsuranceEnabled(true)
                .isSalaryClient(false);

        LoanOfferDTO fourthOffer = new LoanOfferDTO()
                .applicationId(1)
                .requestedAmount(BigDecimal.valueOf(3000000))
                .totalAmount(BigDecimal.valueOf(3150000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(268942.37))
                .rate(BigDecimal.valueOf(4.5))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        loanOffers.add(firstOffer);
        loanOffers.add(secondOffer);
        loanOffers.add(thirdOffer);
        loanOffers.add(fourthOffer);

        when(conveyorService.listOfPossibleLoanOffers(loanApplicationRequestDTO)).thenReturn(loanOffers);

        String request = asJsonString(loanApplicationRequestDTO);

        mockMvc.perform(
                        post("/conveyor/offers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andDo(print())
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.requestedAmount").value(loanOffers.get(0).getRequestedAmount()))
//                .andExpect(jsonPath("$.totalAmount").value(loanOffers.get(0).getTotalAmount()))
//                .andExpect(jsonPath("$.term").value(loanOffers.get(0).getTerm()))
//                .andExpect(jsonPath("$.monthlyPayment").value(loanOffers.get(0).getMonthlyPayment()))
//                .andExpect(jsonPath("$.rate").value(loanOffers.get(0).getRate()))
//                .andExpect(jsonPath("$.isInsuranceEnabled").value(loanOffers.get(0).getIsInsuranceEnabled()))
//                .andExpect(jsonPath("$.isSalaryClient").value(loanOffers.get(0).getIsSalaryClient()));

    }

    @Test
    void loanParameters() throws Exception {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                .amount(BigDecimal.valueOf(700000))
                .term(6)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .gender(ScoringDataDTO.GenderEnum.MALE)
                .birthdate(LocalDate.parse("1996-09-09"))
                .passportSeries("3619")
                .passportNumber("725693")
                .passportIssueDate(LocalDate.parse("2022-09-09"))
                .passportIssueBranch("Ленинградская область")
                .maritalStatus(ScoringDataDTO.MaritalStatusEnum.MARRIED)
                .dependentAmount(100000)
                .employment(new EmploymentDTO()
                        .employmentStatus(EmploymentDTO.EmploymentStatusEnum.EMPLOYED)
                        .employerINN("827461927")
                        .salary(BigDecimal.valueOf(90000))
                        .position(EmploymentDTO.PositionEnum.WORKER)
                        .workExperienceTotal(15)
                        .workExperienceCurrent(20))
                .account("536263827462")
                .isInsuranceEnabled(true)
                .isSalaryClient(false);

        PaymentScheduleElement firstPayment = new PaymentScheduleElement()
                .number(1)
                .date(LocalDate.parse("2022-10-10"))
                .totalPayment(BigDecimal.valueOf(143739.8).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(3544.27))
                .debtPayment(BigDecimal.valueOf(140195.54))
                .remainingDebt(BigDecimal.valueOf(718699).setScale(2, RoundingMode.CEILING));

        PaymentScheduleElement secondPayment = new PaymentScheduleElement()
                .number(2)
                .date(LocalDate.parse("2022-11-10"))
                .totalPayment(BigDecimal.valueOf(143739.8).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(2953.56))
                .debtPayment(BigDecimal.valueOf(140786.25))
                .remainingDebt(BigDecimal.valueOf(574959.2).setScale(2, RoundingMode.CEILING));

        PaymentScheduleElement thirdPayment = new PaymentScheduleElement()
                .number(3)
                .date(LocalDate.parse("2022-12-10"))
                .totalPayment(BigDecimal.valueOf(143739.8).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(2362.85))
                .debtPayment(BigDecimal.valueOf(141376.96))
                .remainingDebt(BigDecimal.valueOf(431219.4).setScale(2, RoundingMode.CEILING));

        PaymentScheduleElement fourthPayment = new PaymentScheduleElement()
                .number(4)
                .date(LocalDate.parse("2023-01-10"))
                .totalPayment(BigDecimal.valueOf(143739.8).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(1772.14))
                .debtPayment(BigDecimal.valueOf(141967.67))
                .remainingDebt(BigDecimal.valueOf(287479.6).setScale(2, RoundingMode.CEILING));

        PaymentScheduleElement fifthPayment = new PaymentScheduleElement()
                .number(5)
                .date(LocalDate.parse("2023-02-10"))
                .totalPayment(BigDecimal.valueOf(143739.8).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(1181.43))
                .debtPayment(BigDecimal.valueOf(142558.38))
                .remainingDebt(BigDecimal.valueOf(143739.8).setScale(2, RoundingMode.CEILING));

        PaymentScheduleElement sixthPayment = new PaymentScheduleElement()
                .number(6)
                .date(LocalDate.parse("2023-03-10"))
                .totalPayment(BigDecimal.valueOf(143739.8).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(590.72))
                .debtPayment(BigDecimal.valueOf(143149.09))
                .remainingDebt(BigDecimal.valueOf(0).setScale(2, RoundingMode.CEILING));

        List<PaymentScheduleElement> paymentScheduleElements = new ArrayList<>();

        paymentScheduleElements.add(firstPayment);
        paymentScheduleElements.add(secondPayment);
        paymentScheduleElements.add(thirdPayment);
        paymentScheduleElements.add(fourthPayment);
        paymentScheduleElements.add(fifthPayment);
        paymentScheduleElements.add(sixthPayment);

        CreditDTO creditDTO = new CreditDTO()
                .amount(BigDecimal.valueOf(700000))
                .term(6)
                .monthlyPayment(BigDecimal.valueOf(143739.8))
                .rate(BigDecimal.valueOf(5))
                .psk(BigDecimal.valueOf(38.221))
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .paymentSchedule(paymentScheduleElements);

        when(conveyorService.loanParameters(scoringDataDTO)).thenReturn(creditDTO);

        String request = asJsonString(scoringDataDTO);

        mockMvc.perform(
                        post("/conveyor/calculation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(creditDTO.getAmount()))
                .andExpect(jsonPath("$.term").value(creditDTO.getTerm()))
                .andExpect(jsonPath("$.monthlyPayment").value(creditDTO.getMonthlyPayment()))
                .andExpect(jsonPath("$.rate").value(creditDTO.getRate()))
                .andExpect(jsonPath("$.psk").value(creditDTO.getPsk()))
                .andExpect(jsonPath("$.isInsuranceEnabled").value(creditDTO.getIsInsuranceEnabled()))
                .andExpect(jsonPath("$.isSalaryClient").value(creditDTO.getIsSalaryClient()));
    }
}