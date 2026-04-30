package tn.esprit.esprit_market.modules.store.dto;

import lombok.*;
import tn.esprit.esprit_market.modules.store.enums.StockStatus;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private boolean active;
    private Date createdAt;

    // ✅ Juste l'id et nom de la store → pas l'objet complet
    private Long storeId;
    private String storeName;

    // ✅ Category simplifié comme Store
    private Long categoryId;
    private String categoryName;

    // ✅ Images simplifiées
    private Long imageId;
    private String imageUrl;

    // ✅ Stock status calculé (OUT_OF_STOCK / LOW_STOCK / IN_STOCK)
    private StockStatus stockStatus;
}