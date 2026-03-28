package tn.esprit.esprit_market.modules.administration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionDTO {
    private Long id;
    private double rate;
    private double amount;
    private Long storeId;
    private String storeName;
}
