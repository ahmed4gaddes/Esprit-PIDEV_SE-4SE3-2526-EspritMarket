package tn.esprit.esprit_market.modules.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.order.entity.Cart;
import tn.esprit.esprit_market.modules.order.entity.Order;
import tn.esprit.esprit_market.modules.event.entities.Ticket;
import tn.esprit.esprit_market.modules.marketing.entity.Sponsorship;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;
import tn.esprit.esprit_market.modules.service.entity.Registration;
import tn.esprit.esprit_market.modules.service.entity.Gamification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean storeActive;

    @Builder.Default
    private boolean isActive = true;

    @Pattern(regexp = "^(\\+?\\d{8,15})?$", message = "Phone number must be valid (8-15 digits)")// modifié
    private String phoneNumber;

    private String address;

    private String profilePicture;

    private double totalSales;
    //@Column(name = "date_of_birth") //ajouté nouvelle
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // ===== INTER-MODULE RELATIONSHIPS =====

    // User 1..* Store (owner)
    @OneToMany(mappedBy = "owner")
    @Builder.Default
    private List<Store> stores = new ArrayList<>();

    // User 1..0..1 Cart
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    // User 1..* Order
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    // User 1..* Sponsorship
    @OneToMany(mappedBy = "sponsor")
    @Builder.Default
    private List<Sponsorship> sponsorships = new ArrayList<>();

    // User 1..* SponsorshipRequest
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<SponsorshipRequest> sponsorshipRequests = new ArrayList<>();

    // User *..1 Ticket
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    // User 1..* Registration
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Registration> registrations = new ArrayList<>();

    // User 1..1 Gamification
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Gamification gamification;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
