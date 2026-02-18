package tn.esprit.esprit_market.Module.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.esprit_market.Module.service.enums.ServiceType;
import tn.esprit.esprit_market.entities.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Internship extends Service {
    private String company;
    private int durationMonths;
    private String companyAddress;
    private String companyTutorName;
    private String companyTutorEmail;
    private String academicTutorName;
    private boolean agreementSigned;
    private Date startDate;
    private Date endDate;
}