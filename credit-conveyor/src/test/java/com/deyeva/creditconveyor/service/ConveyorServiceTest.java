package com.deyeva.creditconveyor.service;

import com.deyeva.creditconveyor.exception.RefusalException;
import com.deyeva.creditconveyor.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConveyorServiceTest {
    ConveyorService conveyorService = new ConveyorService();

    @Test
    void listOfPossibleLoanOffers_happyPath() {
        LoanApplicationRequestDTO loanApplicationRequestDTO = new  LoanApplicationRequestDTO()
                .amount(BigDecimal.valueOf(700000))
                .term(60)
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .email("ivanov_86@gmail.com")
                .birthdate(LocalDate.parse("2000-09-10"))
                .passportSeries("3619")
                .passportNumber("725693");

        List<LoanOfferDTO> actual = conveyorService.listOfPossibleLoanOffers(loanApplicationRequestDTO);

        List<LoanOfferDTO> expected = new ArrayList<>();

        LoanOfferDTO firstLoanOffer = new LoanOfferDTO()
                .applicationId(1)
                .requestedAmount(BigDecimal.valueOf(700000))
                .totalAmount(BigDecimal.valueOf(700000))
                .term(60)
                .monthlyPayment(BigDecimal.valueOf(14193.48))
                .rate(BigDecimal.valueOf(8))
                .isInsuranceEnabled(false)
                .isSalaryClient(false);

        LoanOfferDTO secondLoanOffer = new LoanOfferDTO()
                .applicationId(1)
                .requestedAmount(BigDecimal.valueOf(700000))
                .totalAmount(BigDecimal.valueOf(700000))
                .term(60)
                .monthlyPayment(BigDecimal.valueOf(13860.84))
                .rate(BigDecimal.valueOf(7))
                .isInsuranceEnabled(false)
                .isSalaryClient(true);

        LoanOfferDTO thirdLoanOffer = new LoanOfferDTO()
                .applicationId(1)
                .requestedAmount(BigDecimal.valueOf(700000))
                .totalAmount(BigDecimal.valueOf(850000))
                .term(60)
                .monthlyPayment(BigDecimal.valueOf(16040.56))
                .rate(BigDecimal.valueOf(5))
                .isInsuranceEnabled(true)
                .isSalaryClient(false);

        LoanOfferDTO fourthLoanOffer = new LoanOfferDTO()
                .applicationId(1)
                .requestedAmount(BigDecimal.valueOf(700000))
                .totalAmount(BigDecimal.valueOf(850000))
                .term(60)
                .monthlyPayment(BigDecimal.valueOf(15654.06))
                .rate(BigDecimal.valueOf(4))
                .isInsuranceEnabled(true)
                .isSalaryClient(true);

        expected.add(firstLoanOffer);
        expected.add(secondLoanOffer);
        expected.add(thirdLoanOffer);
        expected.add(fourthLoanOffer);

        assertEquals(actual, expected);
    }

    @Test
    void loanParameters_happyPath() {
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

        CreditDTO actual = conveyorService.loanParameters(scoringDataDTO);

        PaymentScheduleElement firstPayment = new PaymentScheduleElement()
                .number(1)
                .date(LocalDate.now().plusMonths(1))
                .totalPayment(BigDecimal.valueOf(143739.81).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(3544.27))
                .debtPayment(BigDecimal.valueOf(140195.55))
                .remainingDebt(BigDecimal.valueOf(718699.05).setScale(2, RoundingMode.CEILING));

        PaymentScheduleElement secondPayment = new PaymentScheduleElement()
                .number(2)
                .date(LocalDate.now().plusMonths(2))
                .totalPayment(BigDecimal.valueOf(143739.81).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(2953.56))
                .debtPayment(BigDecimal.valueOf(140786.26))
                .remainingDebt(BigDecimal.valueOf(574959.24).setScale(2, RoundingMode.CEILING));

        PaymentScheduleElement thirdPayment = new PaymentScheduleElement()
                .number(3)
                .date(LocalDate.now().plusMonths(3))
                .totalPayment(BigDecimal.valueOf(143739.81).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(2362.85))
                .debtPayment(BigDecimal.valueOf(141376.97))
                .remainingDebt(BigDecimal.valueOf(431219.43).setScale(2, RoundingMode.CEILING));

        PaymentScheduleElement fourthPayment = new PaymentScheduleElement()
                .number(4)
                .date(LocalDate.now().plusMonths(4))
                .totalPayment(BigDecimal.valueOf(143739.81).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(1772.14))
                .debtPayment(BigDecimal.valueOf(141967.68))
                .remainingDebt(BigDecimal.valueOf(287479.62).setScale(2, RoundingMode.CEILING));

        PaymentScheduleElement fifthPayment = new PaymentScheduleElement()
                .number(5)
                .date(LocalDate.now().plusMonths(5))
                .totalPayment(BigDecimal.valueOf(143739.81).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(1181.43))
                .debtPayment(BigDecimal.valueOf(142558.39))
                .remainingDebt(BigDecimal.valueOf(143739.81).setScale(2, RoundingMode.CEILING));

        PaymentScheduleElement sixthPayment = new PaymentScheduleElement()
                .number(6)
                .date(LocalDate.now().plusMonths(6))
                .totalPayment(BigDecimal.valueOf(143739.81).setScale(2, RoundingMode.CEILING))
                .interestPayment(BigDecimal.valueOf(590.72))
                .debtPayment(BigDecimal.valueOf(143149.10).setScale(2, RoundingMode.CEILING))
                .remainingDebt(BigDecimal.valueOf(0).setScale(2, RoundingMode.CEILING));

        List<PaymentScheduleElement> paymentScheduleElements= new ArrayList<>();

        paymentScheduleElements.add(firstPayment);
        paymentScheduleElements.add(secondPayment);
        paymentScheduleElements.add(thirdPayment);
        paymentScheduleElements.add(fourthPayment);
        paymentScheduleElements.add(fifthPayment);
        paymentScheduleElements.add(sixthPayment);

        CreditDTO expected = new CreditDTO()
                .amount(BigDecimal.valueOf(700000))
                .term(6)
                .monthlyPayment(BigDecimal.valueOf(143739.81).setScale(2, RoundingMode.CEILING))
                .rate(BigDecimal.valueOf(5))
                .psk(BigDecimal.valueOf(38.221))
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .paymentSchedule(paymentScheduleElements);

        assertEquals(actual, expected);

    }

    @Test
    void evaluateRateForPrescoring_anotherScript() {
        BigDecimal actual = conveyorService.evaluateRateForPrescoring(false, false, BigDecimal.valueOf(300000), 6);
        assertEquals(actual, BigDecimal.valueOf(9.5));
    }

    @Test
    void evaluateRateForScoring_firstScript() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                .amount(BigDecimal.valueOf(700000))
                .term(6)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .gender(ScoringDataDTO.GenderEnum.FEMALE)
                .birthdate(LocalDate.parse("1980-09-09"))
                .passportSeries("3619")
                .passportNumber("725693")
                .passportIssueDate(LocalDate.parse("2022-09-09"))
                .passportIssueBranch("Ленинградская область")
                .maritalStatus(ScoringDataDTO.MaritalStatusEnum.DIVORCED)
                .dependentAmount(0)
                .employment(new EmploymentDTO()
                        .employmentStatus(EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED)
                        .employerINN("827461927")
                        .salary(BigDecimal.valueOf(90000))
                        .position(EmploymentDTO.PositionEnum.MID_MANAGER)
                        .workExperienceTotal(15)
                        .workExperienceCurrent(20))
                .account("536263827462")
                .isInsuranceEnabled(true)
                .isSalaryClient(false);
        BigDecimal actual = conveyorService.evaluateRateForScoring(scoringDataDTO);

        assertEquals(actual, BigDecimal.valueOf(6));
    }

    @Test
    void evaluateRateForScoring_secondScript() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                .amount(BigDecimal.valueOf(700000))
                .term(6)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .gender(ScoringDataDTO.GenderEnum.NON_BINARY)
                .birthdate(LocalDate.parse("1980-09-09"))
                .passportSeries("3619")
                .passportNumber("725693")
                .passportIssueDate(LocalDate.parse("2022-09-09"))
                .passportIssueBranch("Ленинградская область")
                .maritalStatus(ScoringDataDTO.MaritalStatusEnum.DIVORCED)
                .dependentAmount(0)
                .employment(new EmploymentDTO()
                        .employmentStatus(EmploymentDTO.EmploymentStatusEnum.BUSINESS_OWNER)
                        .employerINN("827461927")
                        .salary(BigDecimal.valueOf(90000))
                        .position(EmploymentDTO.PositionEnum.TOP_MANAGER)
                        .workExperienceTotal(15)
                        .workExperienceCurrent(20))
                .account("536263827462")
                .isInsuranceEnabled(true)
                .isSalaryClient(false);
        BigDecimal actual = conveyorService.evaluateRateForScoring(scoringDataDTO);

        assertEquals(actual, BigDecimal.valueOf(12));
    }

    @Test
    void testExpectedException_workingException() {

        RefusalException exception = Assertions.assertThrows(RefusalException.class, () -> {
            ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                    .amount(BigDecimal.valueOf(700000))
                    .term(6)
                    .firstName("Ivan")
                    .lastName("Ivanov")
                    .middleName("Ivanovich")
                    .gender(ScoringDataDTO.GenderEnum.MALE)
                    .birthdate(LocalDate.parse("1980-09-09"))
                    .passportSeries("3619")
                    .passportNumber("725693")
                    .passportIssueDate(LocalDate.parse("2013-09-09"))
                    .passportIssueBranch("Ленинградская область")
                    .maritalStatus(ScoringDataDTO.MaritalStatusEnum.MARRIED)
                    .dependentAmount(100000)
                    .employment(new EmploymentDTO()
                            .employmentStatus(EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED)
                            .employerINN("827461927")
                            .salary(BigDecimal.valueOf(90000))
                            .position(EmploymentDTO.PositionEnum.WORKER)
                            .workExperienceTotal(12)
                            .workExperienceCurrent(12))
                    .account("536263827462")
                    .isInsuranceEnabled(true)
                    .isSalaryClient(false);

            conveyorService.evaluateRateForScoring(scoringDataDTO);
        });

        Assertions.assertEquals("[An unemployed person cannot be given a loan]", exception.getMessage());
    }

    @Test
    void testExpectedException_salaryException() {

        RefusalException exception = Assertions.assertThrows(RefusalException.class, () -> {
            ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                    .amount(BigDecimal.valueOf(700000))
                    .term(6)
                    .firstName("Ivan")
                    .lastName("Ivanov")
                    .middleName("Ivanovich")
                    .gender(ScoringDataDTO.GenderEnum.MALE)
                    .birthdate(LocalDate.parse("2000-09-09"))
                    .passportSeries("3619")
                    .passportNumber("725693")
                    .passportIssueDate(LocalDate.parse("2013-09-09"))
                    .passportIssueBranch("Ленинградская область")
                    .maritalStatus(ScoringDataDTO.MaritalStatusEnum.MARRIED)
                    .dependentAmount(100000)
                    .employment(new EmploymentDTO()
                            .employmentStatus(EmploymentDTO.EmploymentStatusEnum.EMPLOYED)
                            .employerINN("827461927")
                            .salary(BigDecimal.valueOf(20000))
                            .position(EmploymentDTO.PositionEnum.WORKER)
                            .workExperienceTotal(12)
                            .workExperienceCurrent(12))
                    .account("536263827462")
                    .isInsuranceEnabled(true)
                    .isSalaryClient(false);

            conveyorService.evaluateRateForScoring(scoringDataDTO);
        });

        Assertions.assertEquals("[The loan amount cannot be more than 20 salaries]", exception.getMessage());
    }

    @Test
    void testExpectedException_ageException() {

        RefusalException exception = Assertions.assertThrows(RefusalException.class, () -> {
            ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                    .amount(BigDecimal.valueOf(700000))
                    .term(6)
                    .firstName("Ivan")
                    .lastName("Ivanov")
                    .middleName("Ivanovich")
                    .gender(ScoringDataDTO.GenderEnum.MALE)
                    .birthdate(LocalDate.parse("2013-09-09"))
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
                            .workExperienceTotal(12)
                            .workExperienceCurrent(12))
                    .account("536263827462")
                    .isInsuranceEnabled(true)
                    .isSalaryClient(false);

            conveyorService.evaluateRateForScoring(scoringDataDTO);
        });

        Assertions.assertEquals("[Age restrictions for issuing a loan]", exception.getMessage());
    }

    @Test
    void testExpectedException_workExperienceException() {

        RefusalException exception = Assertions.assertThrows(RefusalException.class, () -> {
            ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                    .amount(BigDecimal.valueOf(700000))
                    .term(6)
                    .firstName("Ivan")
                    .lastName("Ivanov")
                    .middleName("Ivanovich")
                    .gender(ScoringDataDTO.GenderEnum.MALE)
                    .birthdate(LocalDate.parse("2000-09-09"))
                    .passportSeries("3619")
                    .passportNumber("725693")
                    .passportIssueDate(LocalDate.parse("2015-09-09"))
                    .passportIssueBranch("Ленинградская область")
                    .maritalStatus(ScoringDataDTO.MaritalStatusEnum.MARRIED)
                    .dependentAmount(100000)
                    .employment(new EmploymentDTO()
                            .employmentStatus(EmploymentDTO.EmploymentStatusEnum.EMPLOYED)
                            .employerINN("827461927")
                            .salary(BigDecimal.valueOf(90000))
                            .position(EmploymentDTO.PositionEnum.WORKER)
                            .workExperienceTotal(4)
                            .workExperienceCurrent(4))
                    .account("536263827462")
                    .isInsuranceEnabled(true)
                    .isSalaryClient(false);

            conveyorService.evaluateRateForScoring(scoringDataDTO);
        });

        Assertions.assertEquals("[Work experience less than 12 months]", exception.getMessage());
    }

    @Test
    void testExpectedException_newWorkExperienceException() {

        RefusalException exception = Assertions.assertThrows(RefusalException.class, () -> {
            ScoringDataDTO scoringDataDTO = new ScoringDataDTO()
                    .amount(BigDecimal.valueOf(700000))
                    .term(6)
                    .firstName("Ivan")
                    .lastName("Ivanov")
                    .middleName("Ivanovich")
                    .gender(ScoringDataDTO.GenderEnum.MALE)
                    .birthdate(LocalDate.parse("2000-09-09"))
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
                            .workExperienceTotal(12)
                            .workExperienceCurrent(2))
                    .account("536263827462")
                    .isInsuranceEnabled(true)
                    .isSalaryClient(false);

            conveyorService.evaluateRateForScoring(scoringDataDTO);
        });

        Assertions.assertEquals("[Work experience at a new job is less than 12 months]", exception.getMessage());
    }
}