package tn.esprit.esprit_market.modules.administration.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.esprit_market.modules.administration.enums.RuleCategory;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Rule title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    private RuleCategory category;

    @Builder.Default
    private boolean active = true;

    private boolean mandatory;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // Rule *..1 User (createdBy)
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    // Rule *..* Store (appliesTo)
    @ManyToMany
    @JoinTable(name = "rule_store", joinColumns = @JoinColumn(name = "rule_id"), inverseJoinColumns = @JoinColumn(name = "store_id"))
    @Builder.Default
    private Set<Store> appliesTo = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
