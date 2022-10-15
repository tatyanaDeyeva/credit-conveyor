package com.deyeva.application.service;

import com.deyeva.application.exception.RefusalException;
import com.deyeva.application.feign.DealClient;
import com.deyeva.application.model.LoanApplicationRequestDTO;
import com.deyeva.application.model.LoanOfferDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class ApplicationService {

    private final DealClient dealClient;

    private final BigDecimal MIN_AMOUNT = BigDecimal.valueOf(200000);
    private final Integer MIN_TERM = 6;
    private final Integer MIN_AGE = 20;

    public List<LoanOfferDTO> getListOfPossibleLoanOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        doPrescoring(loanApplicationRequestDTO);
        return dealClient.getOffers(loanApplicationRequestDTO);
    }

    public void choiceLoanOffer(LoanOfferDTO loanOfferDTO) {
        dealClient.getLoanOffer(loanOfferDTO);
    }

    public void doPrescoring(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        List<String> refuseNotes = new ArrayList<>();

        Pattern patternForTerm = Pattern.compile("\\d{1,2}");
        Pattern patternForName = Pattern.compile("[а-яА-Я]{2,30}");
        Pattern patternForEmail = Pattern.compile("[\\w\\.]{2,50}@[\\w\\.]{2,20}");
        Pattern patternForBirthdate = Pattern.compile("\\d{4}-[01]\\d-[0123]\\d");
        Pattern patternForPasSeries = Pattern.compile("\\d{4}");
        Pattern patternForPasNumber = Pattern.compile("\\d{6}");

        if (loanApplicationRequestDTO.getAmount().compareTo(MIN_AMOUNT) < 0) {
            refuseNotes.add("The loan amount is insufficient");
        }

        if (loanApplicationRequestDTO.getTerm() < MIN_TERM &&
                !patternForTerm.matcher(loanApplicationRequestDTO.getTerm().toString()).matches()) {
            refuseNotes.add("The loan term is insufficient");
        }

        if (!patternForName.matcher(loanApplicationRequestDTO.getFirstName()).matches()) {
            refuseNotes.add("Invalid first name");
        }

        if (!patternForName.matcher(loanApplicationRequestDTO.getLastName()).matches()) {
            refuseNotes.add("Invalid last name");
        }

        if (!loanApplicationRequestDTO.getMiddleName().isEmpty() &&
                !patternForName.matcher(loanApplicationRequestDTO.getMiddleName()).matches()) {
            refuseNotes.add("Invalid middle name");
        }

        if (!patternForBirthdate
                .matcher(loanApplicationRequestDTO.getBirthdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .matches()) {
            refuseNotes.add("Invalid birthdate");
        }

        if (MIN_AGE > ChronoUnit.YEARS.between(loanApplicationRequestDTO.getBirthdate(), LocalDate.now())) {
            refuseNotes.add("Age restrictions for issuing a loan");
        }

        if (!patternForEmail.matcher(loanApplicationRequestDTO.getEmail()).matches()) {
            refuseNotes.add("Invalid email");
        }

        if (!patternForPasSeries.matcher(loanApplicationRequestDTO.getPassportSeries()).matches()) {
            refuseNotes.add("Invalid passport series");
        }

        if (!patternForPasNumber.matcher(loanApplicationRequestDTO.getPassportNumber()).matches()) {
            refuseNotes.add("Invalid passport number");
        }

        if (!refuseNotes.isEmpty()) {
            throw new RefusalException(refuseNotes);
        }

    }
}
