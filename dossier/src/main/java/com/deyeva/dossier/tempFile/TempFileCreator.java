package com.deyeva.dossier.tempFile;

import com.deyeva.dossier.model.Client;
import com.deyeva.dossier.model.Credit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TempFileCreator {

    public List<String> createTempFile(Credit credit, Client borrower) {

        try {
            File creditContractFile = File.createTempFile("CreditContract", ".txt");
            File creditApplicationFile = File.createTempFile("CreditApplication", ".txt");
            File paymentScheduleFile = File.createTempFile("PaymentSchedule", ".txt");

            FileWriter creditContract = new FileWriter(creditContractFile);
            FileWriter creditApplication = new FileWriter(creditApplicationFile);
            FileWriter paymentSchedule = new FileWriter(paymentScheduleFile);

            creditContract.write("Loan agreement dated " + LocalDate.now());
            creditContract.write(". Individual terms of the consumer loan agreement for the Borrower "
                    + borrower.getMiddleName() + " "
                    + borrower.getFirstName() + " "
                    + borrower.getLastName() + " include: \n");
            creditContract.write("Application id = " + credit.getId());
            creditContract.write("\n Amount: " + credit.getAmount());
            creditContract.write("\n Term: " + credit.getTerm());
            creditContract.write("\n Monthly Payment: " + credit.getMonthlyPayment());
            creditContract.write("\n Psk: " + credit.getPsk());
            creditContract.write("\n Rate: " + credit.getRate());
            creditContract.write("\n IsInsuranceEnabled: " + credit.getIsInsuranceEnabled());
            creditContract.write("\n IsSalaryClient: " + credit.getIsSalaryClient());

            creditContract.close();

            creditApplication.write("Information about the borrower: \n");
            creditApplication.write(borrower.toString());

            creditApplication.close();

            paymentSchedule.write("Loan agreement dated " + LocalDate.now());
            paymentSchedule.write(" has the payment schedule below: \n");
            paymentSchedule.write(credit.getPaymentSchedule().toString());

            paymentSchedule.close();

            List<String> result = new ArrayList();
            result.add(creditContractFile.getAbsolutePath());
            result.add(creditApplicationFile.getAbsolutePath());
            result.add(paymentScheduleFile.getAbsolutePath());

            return result;

        } catch (IOException e) {
            log.debug(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
