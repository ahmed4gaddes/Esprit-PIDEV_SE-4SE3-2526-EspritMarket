package tn.esprit.esprit_market.modules.store.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.esprit_market.modules.store.dto.CategoryDTO;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.entity.Product;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .type(category.getType())
                // ✅ method reference + toList()
                .productIds(category.getProducts()
                        .stream()
                        .map(Product::getId)     // ✅ lambda → method reference
                        .toList())               // ✅ Collectors.toList() → toList()
                .productNames(category.getProducts()
                        .stream()
                        .map(Product::getName)   // ✅ lambda → method reference
                        .toList())               // ✅ Collectors.toList() → toList()
                .storeId(category.getStore() != null ? category.getStore().getId() : null)
                .storeName(category.getStore() != null ? category.getStore().getName() : null)
                .build();
    }

    public Category toEntity(CategoryDTO dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .type(dto.getType())
                .build();
    }
}