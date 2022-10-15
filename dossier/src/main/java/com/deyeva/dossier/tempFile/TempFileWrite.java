package com.deyeva.dossier.tempFile;

import com.deyeva.dossier.model.entity.Client;
import com.deyeva.dossier.model.entity.Credit;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

@Component
@AllArgsConstructor
public class TempFileWrite {

    public String createTempFile(Credit credit, Client borrower) {

        try {
                 File tempFile = File.createTempFile("CreditContract", ".txt");
                 System.out.println(tempFile);

                 FileWriter fileWriter = new FileWriter(tempFile);

                 String str1 = "Loan agreement dated " + LocalDate.now();
                 String str2 = "Individual terms of the consumer loan agreement for the Borrower "
                         + borrower.getMiddleName() + " "
                         + borrower.getFirstName() + " "
                         + borrower.getLastName() + ", "
                         + borrower.getBirthDate() + " year of birth, include:";
                 String str3 = credit.toString();

                 fileWriter.write(str1);
                 fileWriter.write(str2);
                 fileWriter.write(str3);

                 fileWriter.close();

                 return tempFile.getAbsolutePath();

        } catch (IOException e) {
                System.out.println(e.getMessage());
                return "Loan agreement has not been created.";
        }

    }
}
