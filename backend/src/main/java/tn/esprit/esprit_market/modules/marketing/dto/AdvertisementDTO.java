package tn.esprit.esprit_market.modules.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDTO {
    private Long id;
    private String title;
    private String content;
    private double budget;
    private Date startDate;
    private Date endDate;
    private boolean active;
    private Long campaignId;
    private Set<Long> storeIds;
}
