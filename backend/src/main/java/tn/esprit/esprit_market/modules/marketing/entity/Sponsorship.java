package tn.esprit.esprit_market.modules.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.marketing.enums.SponsorshipStatus;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.Date;

@Entity
@Table(name = "sponsorships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sponsorship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    @Enumerated(EnumType.STRING)
    private SponsorshipStatus status;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    // Sponsorship *..1 User (sponsor)
    //Le sponsor est un utilisateur (User). Plusieurs parrainages peuvent être associés à un même utilisateur.
    @ManyToOne
    @JoinColumn(name = "sponsor_id")
    private User sponsor;

    // Sponsorship *..1 MarketingCampaign
    //Le parrainage concerne une campagne
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private MarketingCampaign campaign;
//. C’est le côté "inverse" ; la clé étrangère se trouve dans SponsorshipRequest.
    // Sponsorship 1..0..1 SponsorshipRequest
    @OneToOne(mappedBy = "sponsorship")
    private SponsorshipRequest request;
}
