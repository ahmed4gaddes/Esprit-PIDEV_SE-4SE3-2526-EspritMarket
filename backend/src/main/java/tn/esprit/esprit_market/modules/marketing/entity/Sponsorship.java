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
    @ManyToOne
    @JoinColumn(name = "sponsor_id")
    private User sponsor;

    // Sponsorship *..1 MarketingCampaign
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private MarketingCampaign campaign;

    // Sponsorship 1..0..1 SponsorshipRequest
    @OneToOne(mappedBy = "sponsorship")
    private SponsorshipRequest request;
}
