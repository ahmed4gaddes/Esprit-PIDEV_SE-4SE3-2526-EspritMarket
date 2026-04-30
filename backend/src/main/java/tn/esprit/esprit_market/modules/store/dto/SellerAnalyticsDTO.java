package tn.esprit.esprit_market.modules.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerAnalyticsDTO {
    private double revenue;
    private int ordersCount;
    private double avgRating;
    private int productsCount;
}
