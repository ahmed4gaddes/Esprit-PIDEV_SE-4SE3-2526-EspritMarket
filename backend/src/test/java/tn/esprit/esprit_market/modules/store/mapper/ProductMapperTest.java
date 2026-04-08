package tn.esprit.esprit_market.modules.store.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.store.dto.ProductDTO;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.Store;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ProductImageMapper productImageMapper;

    @InjectMocks
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        // Mockito will inject mocks
    }

    @Test
    void testToDTO_WithAllRelations() {
        Store store = new Store();
        store.setId(10L);
        store.setName("My Store");

        Category category = new Category();
        category.setId(20L);
        category.setName("My Category");

        ProductImage image = new ProductImage();
        image.setId(30L);
        image.setUrl("http://image.com/1.png");

        Product product = new Product();
        product.setId(1L);
        product.setName("Product Name");
        product.setDescription("Desc");
        product.setPrice(150.0);
        product.setStock(10);
        product.setActive(true);
        product.setCreatedAt(new Date());

        product.setStore(store);
        product.setCategory(category);
        product.setImages(List.of(image));

        ProductDTO dto = productMapper.toDTO(product);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Product Name", dto.getName());
        assertEquals("Desc", dto.getDescription());
        assertEquals(150.0, dto.getPrice());
        assertEquals(10, dto.getStock());
        assertTrue(dto.isActive());

        assertEquals(10L, dto.getStoreId());
        assertEquals("My Store", dto.getStoreName());

        assertEquals(20L, dto.getCategoryId());
        assertEquals("My Category", dto.getCategoryName());

        assertEquals(30L, dto.getImageId());
        assertEquals("http://image.com/1.png", dto.getImageUrl());
    }

    @Test
    void testToDTO_WithNullRelations() {
        Product product = new Product();
        product.setId(2L);
        product.setName("Simple Product");

        ProductDTO dto = productMapper.toDTO(product);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertNull(dto.getStoreId());
        assertNull(dto.getCategoryId());
        assertNull(dto.getImageId());
    }
}
