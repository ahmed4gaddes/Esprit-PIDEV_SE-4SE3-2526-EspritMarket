package tn.esprit.esprit_market.modules.service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.esprit_market.modules.service.enums.ServiceType;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "services")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Min(value = 0, message = "Price must be positive")
    private double price;

    @Enumerated(EnumType.STRING)
    private ServiceType type;

    private boolean active = true;

    private String imageUrl;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Service *..1 User (creator)
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    // Service 1..* Calendar
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<ServiceCalendar> calendars = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
