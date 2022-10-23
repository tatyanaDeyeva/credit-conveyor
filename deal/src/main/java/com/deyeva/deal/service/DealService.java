package com.deyeva.deal.service;

import com.deyeva.deal.feign.CreditConveyorClient;
import com.deyeva.deal.kafka.KafkaSender;
import com.deyeva.deal.model.*;
import com.deyeva.deal.model.entity.Application;
import com.deyeva.deal.model.entity.Client;
import com.deyeva.deal.model.entity.Credit;
import com.deyeva.deal.repository.ApplicationRepository;
import com.deyeva.deal.repository.ClientRepository;
import com.deyeva.deal.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealService {
    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final CreditConveyorClient creditConveyorClient;
    private final KafkaSender kafkaSender;

    private static int MIN_INT_FOR_RANDOM_NUMBER = 999;
    private static int MAX_INT_FOR_RANDOM_NUMBER = 9999;

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

        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTO.setStatus(ApplicationStatus.PREAPPROVAL);
        applicationStatusHistoryDTO.setTime(LocalDateTime.now());

        List<ApplicationStatusHistoryDTO> statusHistory = new ArrayList<>();
        statusHistory.add(applicationStatusHistoryDTO);

        application.setStatus(ApplicationStatus.PREAPPROVAL);
        application.setStatusHistory(statusHistory);

        applicationRepository.save(application);

        List<LoanOfferDTO> loanOffers = null;
        
        try {
            loanOffers = creditConveyorClient.getOffers(loanApplicationRequestDTO);
            log.info("Possible loan terms for applicationId: "+application.getId()+" are calculated: " + loanOffers);
        } catch (RuntimeException e) {
            EmailMessage emailMessage = new EmailMessage();
            emailMessage.setApplicationId(application.getId());
            emailMessage.setTheme(EmailMessage.ThemeEnum.APPLICATION_DENIED);
            emailMessage.setAddress(application.getClient().getEmail());

            kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);
        }

        loanOffers.stream()
                .peek(loanOfferDTO -> loanOfferDTO.setApplicationId(application.getId()))
                .collect(Collectors.toList());

        return loanOffers;
    }

    public void choiceLoanOffer(LoanOfferDTO loanOfferDTO) {
        Application application = applicationRepository.findById(loanOfferDTO.getApplicationId())
           .orElseThrow(() -> new EntityNotFoundException("Application with id = "+ loanOfferDTO.getApplicationId()+" not found."));

        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTO.setStatus(ApplicationStatus.APPROVED);
        applicationStatusHistoryDTO.setTime(LocalDateTime.now());
        applicationStatusHistoryDTO.setChangeType(ApplicationStatus.PREAPPROVAL);

        List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();
        if (statusHistory.contains(ApplicationStatus.PREAPPROVAL)){
            statusHistory.add(applicationStatusHistoryDTO);
        } else {
            log.info("The transition to status "+ApplicationStatus.APPROVED+" is incorrect.");
        }


        application.setStatus(ApplicationStatus.APPROVED);
        application.setAppliedOffer(loanOfferDTO);
        application.setStatusHistory(statusHistory);
        application.setCreationDate(LocalDateTime.now());

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setApplicationId(loanOfferDTO.getApplicationId());
        emailMessage.setTheme(EmailMessage.ThemeEnum.FINISH_REGISTRATION);
        emailMessage.setAddress(application.getClient().getEmail());

        kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);
        log.info("Application with id = "+application.getId()+" saved in database.");

        applicationRepository.save(application);
    }

    public void calculatedLoanParameters(Long applicationId, FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
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
        log.info("The loan for applicationId = "+application.getId()+" is calculated: "+creditDTO);

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
        applicationStatusHistoryDTO.setStatus(ApplicationStatus.CC_APPROVED);
        applicationStatusHistoryDTO.setTime(LocalDateTime.now());
        applicationStatusHistoryDTO.setChangeType(ApplicationStatus.APPROVED);

        List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();
        if (statusHistory.contains(ApplicationStatus.APPROVED)){
            statusHistory.add(applicationStatusHistoryDTO);
        } else {
            log.info("The transition to status"+ApplicationStatus.CC_APPROVED+" is incorrect.");
        }

        Employment employment = new Employment();
        employment.setEmploymentStatus(EmploymentStatus.valueOf(finishRegistrationRequestDTO.getEmployment().getEmploymentStatus().getValue()));
        employment.setEmployerInn(finishRegistrationRequestDTO.getEmployment().getEmployerINN());
        employment.setSalary(finishRegistrationRequestDTO.getEmployment().getSalary());
        employment.setPosition(Position.valueOf(finishRegistrationRequestDTO.getEmployment().getPosition().getValue()));
        employment.setWorkExperienceTotal(finishRegistrationRequestDTO.getEmployment().getWorkExperienceTotal());
        employment.setWorkExperienceCurrent(finishRegistrationRequestDTO.getEmployment().getWorkExperienceCurrent());

        application.setStatus(ApplicationStatus.CC_APPROVED);
        application.setStatusHistory(statusHistory);
        application.getClient().setGender(finishRegistrationRequestDTO.getGender());
        application.getClient().setMaritalStatus(MaritalStatus.valueOf(finishRegistrationRequestDTO.getMaritalStatus().getValue()));
        application.getClient().setDependentAmount(finishRegistrationRequestDTO.getDependentAmount());
        application.getClient().setEmployment(employment);
        application.getClient().setAccount(finishRegistrationRequestDTO.getAccount());

        int random_number = MIN_INT_FOR_RANDOM_NUMBER + (int)(Math.random() * ((MAX_INT_FOR_RANDOM_NUMBER - MIN_INT_FOR_RANDOM_NUMBER) + 1));
        application.setSesCode(String.valueOf(random_number));

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setApplicationId(applicationId);
        emailMessage.setTheme(EmailMessage.ThemeEnum.CREATE_DOCUMENTS);
        emailMessage.setAddress(application.getClient().getEmail());

        kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);

        applicationRepository.save(application);
    }

    public void sendDocuments(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id = "+ applicationId +" not found."));

        ApplicationStatusHistoryDTO applicationStatusHistoryDTO = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTO.setStatus(ApplicationStatus.PREPARE_DOCUMENTS);
        applicationStatusHistoryDTO.setTime(LocalDateTime.now());
        applicationStatusHistoryDTO.setChangeType(ApplicationStatus.CC_APPROVED);

        List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();
        statusHistory.add(applicationStatusHistoryDTO);

        if (statusHistory.contains(ApplicationStatus.CC_APPROVED)){
            application.setStatusHistory(statusHistory);
        } else {
            log.info("The transition to status"+ApplicationStatus.PREPARE_DOCUMENTS+" is incorrect.");
        }

        application.setStatus(ApplicationStatus.PREAPPROVAL);

        applicationRepository.save(application);

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setApplicationId(applicationId);
        emailMessage.setTheme(EmailMessage.ThemeEnum.SEND_DOCUMENTS);
        emailMessage.setAddress(application.getClient().getEmail());

        kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);
        log.info("Notification for filling in the data for applicationId = "+application.getId()+" has been sent.");

        ApplicationStatusHistoryDTO applicationStatusHistoryDTOSecond = new ApplicationStatusHistoryDTO();
        applicationStatusHistoryDTOSecond.setStatus(ApplicationStatus.DOCUMENT_CREATED);
        applicationStatusHistoryDTOSecond.setTime(LocalDateTime.now());
        applicationStatusHistoryDTOSecond.setChangeType(ApplicationStatus.PREPARE_DOCUMENTS);

        List<ApplicationStatusHistoryDTO> statusHistorySecond = application.getStatusHistory();
        statusHistorySecond.add(applicationStatusHistoryDTOSecond);

        if (statusHistorySecond.contains(ApplicationStatus.PREPARE_DOCUMENTS)) {
            application.setStatusHistory(statusHistorySecond);
        } else {
            log.info("The transition to status "+ApplicationStatus.DOCUMENT_CREATED+" is incorrect.");
        }

        application.setStatus(ApplicationStatus.DOCUMENT_CREATED);
    }

    public void signDocuments(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id = "+ applicationId +" not found."));

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setApplicationId(applicationId);
        emailMessage.setTheme(EmailMessage.ThemeEnum.SEND_SES);
        emailMessage.setAddress(application.getClient().getEmail());

        kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);
        log.info("The notification for signing the documents for applicationId = "+application.getId()+" has been sent.");
    }

    public void sendCode(Long applicationId, String code) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id = "+ applicationId +" not found."));

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setApplicationId(applicationId);
        emailMessage.setAddress(application.getClient().getEmail());

        if (application.getSesCode().equals(code)) {
            ApplicationStatusHistoryDTO applicationStatusHistoryDTOSecond = new ApplicationStatusHistoryDTO();
            applicationStatusHistoryDTOSecond.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
            applicationStatusHistoryDTOSecond.setTime(LocalDateTime.now());
            applicationStatusHistoryDTOSecond.setChangeType(ApplicationStatus.DOCUMENT_CREATED);

            List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();
            statusHistory.add(applicationStatusHistoryDTOSecond);

            if (statusHistory.contains(ApplicationStatus.DOCUMENT_CREATED)){
                application.setStatusHistory(statusHistory);
            } else {
                log.info("The transition to status"+ApplicationStatus.DOCUMENT_SIGNED+" is incorrect.");
            }

            application.setStatus(ApplicationStatus.DOCUMENT_SIGNED);

            emailMessage.setTheme(EmailMessage.ThemeEnum.CREDIT_ISSUED);
            kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);
            log.info("Notification of successful loan issuance for applicationId = "+application.getId()+" has been sent.");
        } else {
            emailMessage.setTheme(EmailMessage.ThemeEnum.APPLICATION_DENIED);
            kafkaSender.sendMessage(emailMessage.getTheme(), emailMessage);

            throw new IllegalArgumentException();
        }
    }

    public Application getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id = "+ applicationId +" not found."));
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }
}
