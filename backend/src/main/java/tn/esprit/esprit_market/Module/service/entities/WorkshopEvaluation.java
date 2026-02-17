package tn.esprit.esprit_market.Module.service.entities;


import jakarta.persistence.*;
import java.util.Date;

@Entity
public class WorkshopEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // Usually 1-5 stars
    private String comment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date evaluationDate;

    // This links the evaluation to a specific student's registration
    @OneToOne
    @JoinColumn(name = "registration_id")
    private Registration registration;

    // Getters and Setters
}