package tn.esprit.esprit_market.modules.service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workshops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Workshop extends Service {

    private int durationHours;
    private int capacity;
    private int enrolledCount;
    private String prerequisites;
    private String providedMaterial;
    private String difficultyLevel;

    // Workshop 1..* Course
    @OneToMany(mappedBy = "workshop", cascade = CascadeType.ALL)
    private List<Course> courses = new ArrayList<>();

    // Workshop 1..* Registration
    @OneToMany(mappedBy = "workshop", cascade = CascadeType.ALL)
    private List<Registration> registrations = new ArrayList<>();
}
