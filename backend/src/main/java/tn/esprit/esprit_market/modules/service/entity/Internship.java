package tn.esprit.esprit_market.modules.service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "internships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Internship extends Service {

    private String company;
    private int durationMonths;
    private String companyAddress;
    private String companyTutorName;
    private String companyTutorEmail;
    private String academicTutorName;
    private boolean agreementSigned;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;
}
