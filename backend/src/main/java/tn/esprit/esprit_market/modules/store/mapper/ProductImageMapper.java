package tn.esprit.esprit_market.modules.store.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.esprit_market.modules.store.dto.ProductImageDTO;
import tn.esprit.esprit_market.modules.store.entity.ProductImage;

@Component
public class ProductImageMapper {

    public ProductImageDTO toDTO(ProductImage image) {
        return ProductImageDTO.builder()
                .id(image.getId())
                .url(image.getUrl())
                .altText(image.getAltText())
                .order(image.getImageOrder())
                // ✅ juste id + nom du produit
                .productId(image.getProduct().getId())
                .productName(image.getProduct().getName())
                .build();
    }

    public ProductImage toEntity(ProductImageDTO dto) {
        return ProductImage.builder()
                .id(dto.getId())
                .url(dto.getUrl())
                .altText(dto.getAltText())
                .imageOrder(dto.getOrder())
                // ✅ pas de product complet ici
                .build();
    }
}
