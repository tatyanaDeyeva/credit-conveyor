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

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
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

    public List<LoanOfferDTO> getListOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
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

        Application application = new Application();
        application.setClient(client);

        applicationRepository.save(application);

        List<LoanOfferDTO> loanOffers = creditConveyorClient.getOffers(loanApplicationRequestDTO);

        loanOffers.stream()
                .peek(loanOfferDTO -> loanOfferDTO.setApplicationId(application.getId()))
                .collect(Collectors.toList());

        return loanOffers;
    }

    public void choiceLoanOffer(LoanOfferDTO loanOfferDTO) {
        Application application = applicationRepository.findById(loanOfferDTO.getApplicationId())
           .orElseThrow(() -> new EntityNotFoundException("Application with id = "+ loanOfferDTO.getApplicationId()+" not found."));

        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTO.setStatus(ApplicationStatus.PREAPPROVAL);
        applicationStatusHistoryDTO.setTime(LocalDateTime.now());

        List<ApplicationStatusHistoryDTO> statusHistory = new ArrayList<>();
        statusHistory.add(applicationStatusHistoryDTO);

        application.setStatus(ApplicationStatus.PREAPPROVAL);
        application.setAppliedOffer(loanOfferDTO);
        application.setStatusHistory(statusHistory);
        application.setCreationDate(LocalDateTime.now());

        applicationRepository.save(application);
    }

    public void calculatedLoanParameters(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO){
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id = "+ applicationId +" not found."));

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

        Credit credit = new Credit();
        credit.setAmount(creditDTO.getAmount());
        credit.setTerm(creditDTO.getTerm());
        credit.setMonthlyPayment(creditDTO.getMonthlyPayment());
        credit.setRate(creditDTO.getRate());
        credit.setPsk(creditDTO.getPsk());
        credit.setPaymentSchedule(creditDTO.getPaymentSchedule());
        credit.setIsInsuranceEnabled(creditDTO.getIsInsuranceEnabled());
        credit.setIsSalaryClient(creditDTO.getIsSalaryClient());
        credit.setCreditStatus(CreditStatus.CALCULATED);

        creditRepository.save(credit);
        application.setCredit(credit);

        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTO.setStatus(ApplicationStatus.APPROVED);
        applicationStatusHistoryDTO.setTime(LocalDateTime.now());
        applicationStatusHistoryDTO.setChangeType(ApplicationStatus.PREAPPROVAL);

        Employment employment = new Employment();
        employment.setEmploymentStatus(EmploymentStatus.valueOf(finishRegistrationRequestDTO.getEmployment().getEmploymentStatus().getValue()));
        employment.setEmployerInn(finishRegistrationRequestDTO.getEmployment().getEmployerINN());
        employment.setSalary(finishRegistrationRequestDTO.getEmployment().getSalary());
        employment.setPosition(Position.valueOf(finishRegistrationRequestDTO.getEmployment().getPosition().getValue()));
        employment.setWorkExperienceTotal(finishRegistrationRequestDTO.getEmployment().getWorkExperienceTotal());
        employment.setWorkExperienceCurrent(finishRegistrationRequestDTO.getEmployment().getWorkExperienceCurrent());

        application.setStatus(ApplicationStatus.APPROVED);
        application.getStatusHistory().add(applicationStatusHistoryDTO);
        application.getClient().setGender(finishRegistrationRequestDTO.getGender());
        application.getClient().setMaritalStatus(MaritalStatus.valueOf(finishRegistrationRequestDTO.getMaritalStatus().getValue()));
        application.getClient().setDependentAmount(finishRegistrationRequestDTO.getDependentAmount());
        application.getClient().setEmployment(employment);
        application.getClient().setAccount(finishRegistrationRequestDTO.getAccount());



        applicationRepository.save(application);
    }
}
