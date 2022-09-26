package com.deyeva.deal.service;

import com.deyeva.deal.feign.CreditConveyorClient;
import com.deyeva.deal.model.*;
import com.deyeva.deal.model.entity.Application;
import com.deyeva.deal.model.entity.Client;
import com.deyeva.deal.model.entity.Credit;
import com.deyeva.deal.repository.ApplicationRepository;
import com.deyeva.deal.repository.ClientRepository;
import com.deyeva.deal.repository.CreditRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DealService {
    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final CreditConveyorClient creditConveyorClient;

    public DealService(ApplicationRepository applicationRepository, ClientRepository clientRepository, CreditRepository creditRepository, CreditConveyorClient creditConveyorClient) {
        this.applicationRepository = applicationRepository;
        this.clientRepository = clientRepository;
        this.creditRepository = creditRepository;
        this.creditConveyorClient = creditConveyorClient;
    }

    public List<LoanOfferDTO> listOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        Client client = new Client();

        Passport passport = new Passport();
        passport.setSeries(loanApplicationRequestDTO.getPassportSeries());
        passport.setNumber(loanApplicationRequestDTO.getPassportNumber());

        client.setFirstName(loanApplicationRequestDTO.getFirstName());
        client.setLastName(loanApplicationRequestDTO.getLastName());
        client.setMiddleName(loanApplicationRequestDTO.getMiddleName());
        client.setEmail(loanApplicationRequestDTO.getEmail());
        client.setBirthDate(loanApplicationRequestDTO.getBirthdate());
        client.setPassport(passport);

        clientRepository.save(client);

        Credit credit = new Credit();
        creditRepository.save(credit);

        Application application = new Application();
        application.setClient(client);
        application.setCredit(credit);

        applicationRepository.save(application);

        List<LoanOfferDTO> loanOffers = creditConveyorClient.getOffers(loanApplicationRequestDTO);

        loanOffers.stream()
                .peek(loanOfferDTO -> loanOfferDTO.setApplicationId(application.getId()))
                .collect(Collectors.toList());

        return loanOffers;
    }

    public void loanOffer(LoanOfferDTO loanOfferDTO) {
        Application application = applicationRepository.getReferenceById(loanOfferDTO.getApplicationId());

        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTO.setStatus(ApplicationStatus.PREAPPROVAL);
        applicationStatusHistoryDTO.setTime(LocalDateTime.now());

        List<ApplicationStatusHistoryDTO> statusHistory = new ArrayList<>();
        statusHistory.add(applicationStatusHistoryDTO);

        application.setStatus(ApplicationStatus.PREAPPROVAL);
        application.setAppliedOffer(loanOfferDTO);
        application.setStatusHistory(statusHistory);

        applicationRepository.save(application);
    }

    public void loanParameters(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO){
        Application application = applicationRepository.getReferenceById(applicationId);

        ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                .amount(application.getAppliedOffer().getRequestedAmount())
                .term(application.getAppliedOffer().getTerm())
                .firstName(application.getClient().getFirstName())
                .lastName(application.getClient().getLastName())
                .middleName(application.getClient().getMiddleName())
                .gender(ScoringDataDTO.GenderEnum.valueOf(finishRegistrationRequestDTO.getGender().toString()))
                .birthdate(application.getClient().getBirthDate())
                .passportSeries(application.getClient().getPassport().getSeries())
                .passportNumber(application.getClient().getPassport().getNumber())
                .passportIssueDate(finishRegistrationRequestDTO.getPassportIssueDate())
                .passportIssueBranch(finishRegistrationRequestDTO.getPassportIssueBrach())
                .maritalStatus(ScoringDataDTO.MaritalStatusEnum.valueOf(finishRegistrationRequestDTO.getMaritalStatus().getValue()))
                .dependentAmount(finishRegistrationRequestDTO.getDependentAmount())
                .employment(finishRegistrationRequestDTO.getEmployment())
                .account(finishRegistrationRequestDTO.getAccount())
                .isInsuranceEnabled(application.getAppliedOffer().getIsInsuranceEnabled())
                .isSalaryClient(application.getAppliedOffer().getIsSalaryClient());

        CreditDTO creditDTO = creditConveyorClient.getLoanOffer(scoringDataDTO);

        Credit credit = application.getCredit();
        credit.setAmount(creditDTO.getAmount());
        credit.setTerm(creditDTO.getTerm());
        credit.setMonthly_payment(creditDTO.getMonthlyPayment());
        credit.setRate(creditDTO.getRate());
        credit.setPsk(creditDTO.getPsk());
        credit.setPayment_schedule(creditDTO.getPaymentSchedule());
        credit.setIs_insurance_enabled(creditDTO.getIsInsuranceEnabled());
        credit.setIs_salary_client(credit.getIs_salary_client());
        credit.setCredit_status(CreditStatus.CALCULATED);

        application.setCredit(credit);

        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTO.setStatus(ApplicationStatus.APPROVED);
        applicationStatusHistoryDTO.setTime(LocalDateTime.now());
        applicationStatusHistoryDTO.setChangeType(ApplicationStatus.PREAPPROVAL);

        application.setStatus(ApplicationStatus.APPROVED);
        application.getStatusHistory().add(applicationStatusHistoryDTO);

        applicationRepository.save(application);
    }
}
