package tn.esprit.esprit_market.modules.store.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.esprit_market.modules.store.dto.ProductDTO;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.enums.StockStatus;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    /**
     * Calcule le StockStatus dynamiquement à partir du stock réel du produit.
     * Règles :
     *   stock == 0          → OUT_OF_STOCK
     *   0 < stock <= seuil  → LOW_STOCK  (seuil par défaut = 5)
     *   stock > seuil       → IN_STOCK
     */
    private StockStatus resolveStockStatus(Product product) {
        int stock     = product.getStock();
        int threshold = product.getStockThreshold(); // valeur par défaut = 5

        if (stock == 0) {
            return StockStatus.OUT_OF_STOCK;
        } else if (stock <= threshold) {
            return StockStatus.LOW_STOCK;
        } else {
            return StockStatus.IN_STOCK;
        }
    }

    public ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())

                // ✅ null check Store
                .storeId(product.getStore() != null ? product.getStore().getId() : null)
                .storeName(product.getStore() != null ? product.getStore().getName() : null)

                // ✅ null check Category
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)

                .imageUrl(product.getImageUrl())

                // ✅ Calculé dynamiquement — corrige les anciens produits en BD
                .stockStatus(resolveStockStatus(product))

                .build();
    }
}
