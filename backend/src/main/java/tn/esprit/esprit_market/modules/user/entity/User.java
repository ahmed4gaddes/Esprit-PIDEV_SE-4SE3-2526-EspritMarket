package tn.esprit.esprit_market.modules.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.esprit_market.modules.user.enums.Role;

import java.util.Date;

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

    @Pattern(regexp = "^(\\+?[0-9]{8,15})?$", message = "Phone number must be valid (8-15 digits)")
    private String phoneNumber;

    private String address;

    private String profilePicture;

    private double totalSales;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
