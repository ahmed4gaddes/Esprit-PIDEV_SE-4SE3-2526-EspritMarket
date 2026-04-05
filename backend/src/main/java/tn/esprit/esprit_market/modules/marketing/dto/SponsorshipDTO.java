package tn.esprit.esprit_market.modules.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.marketing.enums.SponsorshipStatus;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorshipDTO {
    private Long id;
    private double amount;
    private SponsorshipStatus status;
    private Date startDate;
    private Date endDate;
    private Long sponsorId;
    private Long campaignId;
    private Long requestId;
}
