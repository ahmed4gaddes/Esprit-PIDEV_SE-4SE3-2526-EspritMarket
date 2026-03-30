package tn.esprit.esprit_market.modules.store.controller;

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
import tn.esprit.esprit_market.modules.store.mapper.ProductMapper;
import tn.esprit.esprit_market.modules.store.service.IproductService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestControllerProductTest {

    @Mock
    private IproductService iproductService;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private RestControllerProduct controller;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        Store store = new Store();
        store.setId(1L);
        store.setName("Ma Boutique");

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronique");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop ESPRIT");
        product.setDescription("PC portable");
        product.setPrice(1500.0);
        product.setStock(10);
        product.setActive(true);
        product.setStore(store);
        product.setCategory(category);

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Laptop ESPRIT");
        productDTO.setPrice(1500.0);
        productDTO.setStoreId(1L);
        productDTO.setCategoryId(1L);
    }

    @Test
    void testAddProduct() {
        when(iproductService.addProduct(any(Product.class))).thenReturn(product);
        when(iproductService.getProductById(1L)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = controller.addProduct(productDTO);

        assertNotNull(result);
        assertEquals("Laptop ESPRIT", result.getName());
        verify(iproductService).addProduct(any(Product.class));
    }

    @Test
    void testUpdateProduct() {
        when(iproductService.updateProduct(any(Product.class), eq(1L))).thenReturn(product);
        when(iproductService.getProductById(1L)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = controller.updateProduct(1L, productDTO);

        assertNotNull(result);
        assertEquals("Laptop ESPRIT", result.getName());
        verify(iproductService).updateProduct(any(Product.class), eq(1L));
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(iproductService).deleteProduct(1L);

        controller.deleteProduct(1L);

        verify(iproductService).deleteProduct(1L);
    }

    @Test
    void testGetProductById() {
        when(iproductService.getProductById(1L)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = controller.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(iproductService).getProductById(1L);
    }

    @Test
    void testGetAllProducts() {
        when(iproductService.getAllProducts()).thenReturn(Arrays.asList(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        List<ProductDTO> results = controller.getAllProducts();

        assertEquals(1, results.size());
        verify(iproductService).getAllProducts();
    }
}
