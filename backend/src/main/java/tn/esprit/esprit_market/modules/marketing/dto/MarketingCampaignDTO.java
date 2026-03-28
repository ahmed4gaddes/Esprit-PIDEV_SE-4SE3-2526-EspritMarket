package tn.esprit.esprit_market.modules.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingCampaignDTO {
    private Long id;
    private String name;
    private String objective;
    private double budget;
    private String channel;
}
