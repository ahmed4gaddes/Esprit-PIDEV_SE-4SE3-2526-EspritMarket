package tn.esprit.esprit_market.modules.marketing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.esprit_market.modules.store.entity.Store;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "advertisements")//annonce
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Advertisement title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Min(value = 0, message = "Budget must be positive")
    private double budget;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Builder.Default
    private boolean active = true;

    // Advertisement *..1 MarketingCampaign
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private MarketingCampaign campaign;

    // Advertisement *..* Store
    @ManyToMany
    @JoinTable(name = "advertisement_store", joinColumns = @JoinColumn(name = "advertisement_id"), inverseJoinColumns = @JoinColumn(name = "store_id"))
    @Builder.Default
    private Set<Store> stores = new HashSet<>();
}
