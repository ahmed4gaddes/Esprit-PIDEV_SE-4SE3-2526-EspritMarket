package tn.esprit.esprit_market.Module.service.entities;

import jakarta.persistence.*;
import tn.esprit.esprit_market.Module.service.enums.ServiceType;
import tn.esprit.esprit_market.entities.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Entity
public class Workshop extends Service {
    private int durationHours;
    private int capacity;
    private int enrolledCount;
    private String prerequisites;
    private String providedMaterial;
    private String difficultyLevel;

    //@OneToMany(mappedBy = "workshop")
    //private List<Course> courses;"

    @OneToMany(mappedBy = "workshop")
    private List<Registration> registrations;
}