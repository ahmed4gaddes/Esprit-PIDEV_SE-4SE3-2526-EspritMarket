package tn.esprit.esprit_market.modules.store.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tn.esprit.esprit_market.modules.store.dto.CategoryDTO;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.enums.CategoryType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        categoryMapper = new CategoryMapper();
    }

    @Test
    void testToDTO() {
        Store store = new Store();
        store.setId(5L);
        store.setName("Category Store");

        Product p1 = new Product();
        p1.setId(10L);
        p1.setName("Prod1");

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Gizmos");
        category.setType(CategoryType.DIGITAL);
        category.setStore(store);
        category.setProducts(List.of(p1));

        CategoryDTO dto = categoryMapper.toDTO(category);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Electronics", dto.getName());
        assertEquals("Gizmos", dto.getDescription());
        assertEquals(CategoryType.DIGITAL, dto.getType());

        assertEquals(5L, dto.getStoreId());
        assertEquals("Category Store", dto.getStoreName());

        assertEquals(1, dto.getProductIds().size());
        assertEquals(10L, dto.getProductIds().get(0));
        assertEquals("Prod1", dto.getProductNames().get(0));
    }

    @Test
    void testToEntity() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(2L);
        dto.setName("Clothes");
        dto.setDescription("Wearables");
        dto.setType(CategoryType.PHYSICAL);

        Category entity = categoryMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("Clothes", entity.getName());
        assertEquals("Wearables", entity.getDescription());
        assertEquals(CategoryType.PHYSICAL, entity.getType());
    }
}
