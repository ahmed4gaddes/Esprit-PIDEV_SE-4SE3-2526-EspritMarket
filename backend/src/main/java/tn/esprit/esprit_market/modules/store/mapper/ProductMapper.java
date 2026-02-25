package tn.esprit.esprit_market.modules.store.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.esprit_market.modules.store.dto.ProductDTO;
import tn.esprit.esprit_market.modules.store.entity.Product;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryMapper categoryMapper;
    private final ProductImageMapper productImageMapper;

    public ProductDTO toDTO(Product product) {
            return ProductDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .active(product.isActive())
                    .createdAt(product.getCreatedAt())

                    // ✅ Store → id + name seulement
                    .storeId(product.getStore().getId())
                    .storeName(product.getStore().getName())

                    // ✅ Category → id + name seulement
                    .categoryId(product.getCategory().getId())
                    .categoryName(product.getCategory().getName())

                    // ✅ Image → id + url seulement
                    .imageId(product.getImages().isEmpty() ? null : product.getImages().get(0).getId())
                    .imageUrl(product.getImages().isEmpty() ? null : product.getImages().get(0).getUrl())

                    .build();
        }
}
