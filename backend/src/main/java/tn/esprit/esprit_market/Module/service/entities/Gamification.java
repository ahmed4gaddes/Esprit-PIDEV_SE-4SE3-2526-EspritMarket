package tn.esprit.esprit_market.Module.service.entities;

import jakarta.persistence.*;
import tn.esprit.esprit_market.Module.service.enums.ServiceType;
import tn.esprit.esprit_market.Module.service.enums.ValidationStatus;
import tn.esprit.esprit_market.entities.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Entity
public class Gamification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int points;
    private int level;
    private String badge;

    @OneToOne
    private User user;
}