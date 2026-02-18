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