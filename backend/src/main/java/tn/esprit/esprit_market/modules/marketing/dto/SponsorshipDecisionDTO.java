package tn.esprit.esprit_market.modules.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorshipDecisionDTO {
    public boolean approved;
    public String designUrl;
    public String note;
}
