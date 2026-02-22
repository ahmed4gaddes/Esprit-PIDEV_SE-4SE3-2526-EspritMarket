package tn.esprit.esprit_market.modules.service.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.service.enums.RegistrationStatus;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;

@Entity
@Table(name = "registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status;

    private boolean attendanceConfirmed;
    private double evaluationScore;
    private String comment;

    // Registration *..1 Workshop
    @ManyToOne
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;

    // Registration *..1 User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Registration 1..0..1 WorkshopEvaluation
    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL)
    private WorkshopEvaluation evaluation;
}
