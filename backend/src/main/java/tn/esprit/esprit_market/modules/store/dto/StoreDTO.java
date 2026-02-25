package tn.esprit.esprit_market.modules.store.dto;

import lombok.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDTO {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private Date createdAt;

    private List<Long> productIds;      // juste les ids
    private List<String> productNames;
    private List<Long> advertisementIds;
    private List<String> advertisementTitles;

    // ✅ Commissions simplifiées
    private List<Long> commissionIds;

    // ✅ Rules simplifiées
    private List<Long> ruleIds;
    private List<String> ruleTitles;
}