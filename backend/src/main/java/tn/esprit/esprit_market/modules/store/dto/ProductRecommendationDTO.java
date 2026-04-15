package tn.esprit.esprit_market.modules.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor

@Data
@AllArgsConstructor
public class ProductRecommendationDTO {
    private Long id;
    private String name;
    private Double price;
    //private String imageUrl;
    private String categoryName;
}