package tn.esprit.esprit_market.modules.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.marketing.entity.Advertisement;
import tn.esprit.esprit_market.modules.administration.entity.Commission;
import tn.esprit.esprit_market.modules.administration.entity.Rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Store name is required")
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Builder.Default
    private boolean active = true;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnoreProperties({"store"})
    private List<Product> products = new ArrayList<>();
    @ManyToMany(mappedBy = "stores")
    @Builder.Default
    private Set<Advertisement> advertisements = new HashSet<>();
    @OneToMany(mappedBy = "store")
    @Builder.Default
    @JsonIgnoreProperties({"store"})
    private List<Commission> commissions = new ArrayList<>();
    @ManyToMany(mappedBy = "appliesTo")
    @Builder.Default
    @JsonIgnoreProperties({"appliesTo"})
    private Set<Rule> rules = new HashSet<>();
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
