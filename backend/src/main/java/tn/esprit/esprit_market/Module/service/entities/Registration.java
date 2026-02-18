package tn.esprit.esprit_market.Module.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.esprit_market.Module.service.enums.RegistrationStatus;
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
public class Registration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date registrationDate;
    private boolean attendanceConfirmed;
    private double evaluationScore;
    private String comment;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status;

    @ManyToOne
    private User user;

    @ManyToOne
    private Workshop workshop;

    @OneToOne(mappedBy = "registration")
    private WorkshopEvaluation evaluation;
}