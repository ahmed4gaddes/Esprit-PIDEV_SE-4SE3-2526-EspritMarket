package tn.esprit.esprit_market.Module.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.esprit_market.Module.service.enums.ServiceType;
import tn.esprit.esprit_market.entities.User;

import tn.esprit.esprit_market.Module.service.entities.Calendar;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private double price;
    private boolean active;

    @Enumerated(EnumType.STRING)
    private ServiceType type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Association with User (Creator)
    @ManyToOne
    private User creator;

    // Association with Calendar
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<Calendar> availableSlots;
}