package tn.esprit.esprit_market.modules.marketing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "marketing_campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketingCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Campaign name is required")
    @Column(nullable = false)
    private String name;

    private String objective;

    @Min(value = 0, message = "Budget must be positive")
    private double budget;

    private String channel;

    // MarketingCampaign 1..* Advertisement
    //Une campagne peut contenir plusieurs annonces
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Advertisement> advertisements = new ArrayList<>();

    // MarketingCampaign 1..* Sponsorship
    //Une campagne peut recevoir plusieurs parrainages
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Sponsorship> sponsorships = new ArrayList<>();

}
