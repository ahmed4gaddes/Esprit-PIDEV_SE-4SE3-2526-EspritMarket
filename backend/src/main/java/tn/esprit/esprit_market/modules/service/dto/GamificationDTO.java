package tn.esprit.esprit_market.modules.service.dto;

import lombok.Data;

@Data
public class GamificationDTO {
    private Long id;
    private int points;
    private int level;
    private String badge;

    // Relation
    private Long userId;
}
