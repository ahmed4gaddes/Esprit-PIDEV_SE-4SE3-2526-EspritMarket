package tn.esprit.esprit_market.modules.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDTO {
    private Long id;
    private String url;
    private String altText;
    private int order;
    // ✅ Pas de Product ici
    private Long productId;      // juste l'id
    private String productName;  // juste le nom
}