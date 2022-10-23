package com.deyeva.application.controller;

import com.deyeva.application.model.LoanApplicationRequestDTO;
import com.deyeva.application.model.LoanOfferDTO;
import com.deyeva.application.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApplicationControllerTest extends AbstractControllerTest {

    @MockBean
    private ApplicationService applicationService;

    @Test
    void getListOfPossibleLoanOffers() throws Exception{
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

        when(applicationService.getListOfPossibleLoanOffers(loanApplicationRequestDTO)).thenReturn(loanOffers);

        String request = asJsonString(loanApplicationRequestDTO);

        mockMvc.perform(
                        post("/application")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void choiceLoanOffer() throws Exception {
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(700000))
                .totalAmount(BigDecimal.valueOf(700000))
                .term(60)
                .monthlyPayment(BigDecimal.valueOf(14193.48))
                .rate(BigDecimal.valueOf(8))
                .isInsuranceEnabled(false)
                .isSalaryClient(false);

        doNothing().when(applicationService).choiceLoanOffer(loanOfferDTO);

        String request = asJsonString(loanOfferDTO);

        mockMvc.perform(
                        put("/application/offer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andDo(print())
                .andExpect(status().isOk());
    }
}