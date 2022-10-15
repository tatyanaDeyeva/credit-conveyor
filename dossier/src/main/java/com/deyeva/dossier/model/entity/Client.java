package com.deyeva.dossier.model.entity;

import com.deyeva.dossier.model.Employment;
import com.deyeva.dossier.model.Gender;
import com.deyeva.dossier.model.MaritalStatus;
import com.deyeva.dossier.model.Passport;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Client {
    private Long id;
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String email;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private Passport passport;
    private Employment employment;
    private String account;
}
