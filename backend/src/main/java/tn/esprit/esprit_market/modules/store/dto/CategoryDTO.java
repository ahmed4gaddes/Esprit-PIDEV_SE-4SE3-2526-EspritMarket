package tn.esprit.esprit_market.modules.store.dto;


import lombok.*;
import tn.esprit.esprit_market.modules.store.enums.CategoryType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private CategoryType type;
    // ✅ Pas de List<Product> ici
    private List<Long> productIds;        // juste les ids
    private List<String> productNames;
}