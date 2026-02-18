package tn.esprit.esprit_market.Module.service.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.esprit_market.Module.service.enums.CertificationLevel;
import tn.esprit.esprit_market.Module.service.enums.ServiceType;
import tn.esprit.esprit_market.Module.service.enums.ValidationStatus;
import tn.esprit.esprit_market.entities.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private int durationHours;
    private boolean mandatory;
    @Column(length = 1000)
    private String description;
    private String objectives;
    @Column(name = "course_order")
    private int order;

    @ManyToOne
    private Workshop workshop;
}