package tn.esprit.esprit_market.modules.store.dto;

import lombok.*;
import tn.esprit.esprit_market.modules.store.enums.MovementType;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementDTO {
    private Long id;
    private int quantity;
    private MovementType type;
    private Date date;
    //private String reason;
    // ✅ Juste l'id et nom du produit → pas l'objet complet
    private Long productId;
    private String productName;
}