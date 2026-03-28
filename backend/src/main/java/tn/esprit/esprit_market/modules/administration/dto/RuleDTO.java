package tn.esprit.esprit_market.modules.administration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.esprit_market.modules.administration.enums.RuleCategory;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleDTO {
    private Long id;
    private String title;
    private String text;
    private RuleCategory category;
    private boolean active;
    private boolean mandatory;
    private Date createdAt;
    private Date updatedAt;
    private Long createdById;
    private String createdByName;
    private Set<Long> appliesToStoreIds;
}
