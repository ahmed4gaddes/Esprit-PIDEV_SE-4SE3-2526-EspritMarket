package tn.esprit.esprit_market.modules.store.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.esprit_market.modules.store.dto.StockMovementDTO;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;

@Component
public class StockMovementMapper {

    public StockMovementDTO toDTO(StockMovement stockMovement) {
        return StockMovementDTO.builder()
                .id(stockMovement.getId())
                .quantity(stockMovement.getQuantity())
                .type(stockMovement.getType())
                .date(stockMovement.getDate())
                // ✅ Juste id et nom du produit
                .productId(stockMovement.getProduct().getId())
                .productName(stockMovement.getProduct().getName())
                .build();
    }

    public StockMovement toEntity(StockMovementDTO dto, Product product) {
        return StockMovement.builder()
                .id(dto.getId())
                .quantity(dto.getQuantity())
                .type(dto.getType())
                .date(dto.getDate())
                .product(product)
                .build();
    }
}