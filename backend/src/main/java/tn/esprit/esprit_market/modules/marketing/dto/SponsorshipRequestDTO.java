package tn.esprit.esprit_market.modules.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorshipRequestDTO {
    private Long id;
    private String offerTitle;
    private String offerDescription;
    private Double budget;
    private String message;
    private Date date;
    private String state;
    private String sponsorDesignUrl;
    private String sponsorNote;
    private Long companyId;
    private String companyName;
    private Long sponsorId;
    private String sponsorName;
}
