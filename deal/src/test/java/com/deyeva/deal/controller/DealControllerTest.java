package com.deyeva.deal.controller;

import com.deyeva.deal.model.*;
import com.deyeva.deal.service.DealService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DealControllerTest extends AbstractControllerTest{

    @MockBean
    private DealService dealService;

    @Test
    void listOfPossibleLoanOffers() throws Exception{
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
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(3000000))
                .totalAmount(BigDecimal.valueOf(3000000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(261659.37))
                .rate(BigDecimal.valueOf(8.5))
                .isInsuranceEnabled(false)
                .isSalaryClient(false);

        LoanOfferDTO secondOffer = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(3000000))
                .totalAmount(BigDecimal.valueOf(3000000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(260272.26))
                .rate(BigDecimal.valueOf(7.5))
                .isInsuranceEnabled(false)
                .isSalaryClient(true);

        LoanOfferDTO thirdOffer = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(3000000))
                .totalAmount(BigDecimal.valueOf(3150000))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(270385.89))
                .rate(BigDecimal.valueOf(5.5))
                .isInsuranceEnabled(true)
                .isSalaryClient(false);

        LoanOfferDTO fourthOffer = new LoanOfferDTO()
                .applicationId(1L)
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

        when(dealService.listOfPossibleLoanOffers(loanApplicationRequestDTO)).thenReturn(loanOffers);

        String request = asJsonString(loanApplicationRequestDTO);

        mockMvc.perform(
                        post("/deal/application")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void loanOffer() throws Exception {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(700000))
                .totalAmount(BigDecimal.valueOf(700000))
                .term(60)
                .monthlyPayment(BigDecimal.valueOf(14193.48))
                .rate(BigDecimal.valueOf(8))
                .isInsuranceEnabled(false)
                .isSalaryClient(false);

        String request = asJsonString(loanOfferDTO);

        mockMvc.perform(
                put("/deal/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void loanParameters() throws Exception {

        FinishRegistrationRequestDTO finishRegistrationRequestDTO = new FinishRegistrationRequestDTO();
        finishRegistrationRequestDTO.setGender(Gender.MALE);
        finishRegistrationRequestDTO.setMaritalStatus(FinishRegistrationRequestDTO.MaritalStatusEnum.MARRIED);
        finishRegistrationRequestDTO.setDependentAmount(1);
        finishRegistrationRequestDTO.setPassportIssueDate(LocalDate.now());
        finishRegistrationRequestDTO.setPassportIssueBrach("605-234");
        finishRegistrationRequestDTO.setAccount("536263827462");

        EmploymentDTO employment = new EmploymentDTO();
        employment.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.EMPLOYED);
        employment.setEmployerINN("827461927");
        employment.setSalary(BigDecimal.valueOf(90000));
        employment.setPosition(EmploymentDTO.PositionEnum.MID_MANAGER);
        employment.setWorkExperienceTotal(18);
        employment.setWorkExperienceCurrent(5);

        finishRegistrationRequestDTO.setEmployment(employment);

        String request = asJsonString(finishRegistrationRequestDTO);

        mockMvc.perform(
                        put("/deal/calculate/{applicationId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andDo(print())
                .andExpect(status().isOk());
    }
}