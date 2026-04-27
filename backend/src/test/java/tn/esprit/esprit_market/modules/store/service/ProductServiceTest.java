package tn.esprit.esprit_market.modules.store.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryCategory;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStore;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private IRepositoryProduct irepositoryproduct;

    @Mock
    private IRepositoryStore iRepositoryStore;

    @Mock
    private IRepositoryCategory iRepositoryCategory;

    @InjectMocks
    private ProductService productService;

    private Product fakeProduct;
    private Store fakeStore;
    private Category fakeCategory;

    @BeforeEach
    void setUp() {
        fakeStore = new Store();
        fakeStore.setId(1L);
        fakeStore.setName("Ma Boutique");

        fakeCategory = new Category();
        fakeCategory.setId(1L);
        fakeCategory.setName("Electronique");

        fakeProduct = new Product();
        fakeProduct.setId(1L);
        fakeProduct.setName("Laptop ESPRIT");
        fakeProduct.setDescription("PC portable performant");
        fakeProduct.setPrice(1500.0);
        fakeProduct.setStock(10);
        fakeProduct.setActive(true);
        fakeProduct.setStore(fakeStore);
        fakeProduct.setCategory(fakeCategory);
    }

    // ==================== addProduct ====================
    @Test
    void testAddProduct_Success() {
        when(iRepositoryStore.findById(1L)).thenReturn(Optional.of(fakeStore));
        when(iRepositoryCategory.findById(1L)).thenReturn(Optional.of(fakeCategory));
        when(irepositoryproduct.save(any(Product.class))).thenReturn(fakeProduct);

        Product result = productService.addProduct(fakeProduct);

        assertNotNull(result);
        assertEquals("Laptop ESPRIT", result.getName());
        assertEquals(1500.0, result.getPrice());
        verify(irepositoryproduct, times(1)).save(any(Product.class));
    }

    @Test
    void testAddProduct_StoreNotFound_ThrowsException() {
        when(iRepositoryStore.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.addProduct(fakeProduct));
        verify(irepositoryproduct, never()).save(any());
    }

    @Test
    void testAddProduct_CategoryNotFound_ThrowsException() {
        when(iRepositoryStore.findById(1L)).thenReturn(Optional.of(fakeStore));
        when(iRepositoryCategory.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.addProduct(fakeProduct));
        verify(irepositoryproduct, never()).save(any());
    }

    // ==================== getProductById ====================
    @Test
    void testGetProductById_Found() {
        when(irepositoryproduct.findById(1L)).thenReturn(Optional.of(fakeProduct));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop ESPRIT", result.getName());
    }

    @Test
    void testGetProductById_NotFound_ThrowsException() {
        when(irepositoryproduct.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productService.getProductById(99L));
    }

    // ==================== getAllProducts ====================
    @Test
    void testGetAllProducts_ReturnsList() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Clavier RGB");
        when(irepositoryproduct.findAll()).thenReturn(Arrays.asList(fakeProduct, product2));

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ==================== updateProduct ====================
    @Test
    void testUpdateProduct_Success() {
        Product updatedData = new Product();
        updatedData.setName("Laptop ESPRIT Pro");
        updatedData.setDescription("Version améliorée");
        updatedData.setPrice(2000.0);
        updatedData.setStock(5);
        updatedData.setActive(true);
        updatedData.setStore(fakeStore);
        updatedData.setCategory(fakeCategory);

        when(irepositoryproduct.findById(1L)).thenReturn(Optional.of(fakeProduct));
        when(irepositoryproduct.save(any(Product.class))).thenReturn(fakeProduct);

        Product result = productService.updateProduct(updatedData, 1L);

        assertNotNull(result);
        verify(irepositoryproduct, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound_ThrowsException() {
        when(irepositoryproduct.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.updateProduct(new Product(), 99L));
    }

    // ==================== deleteProduct ====================
    @Test
    void testDeleteProduct_Success() {
        doNothing().when(irepositoryproduct).deleteById(1L);

        productService.deleteProduct(1L);

        verify(irepositoryproduct, times(1)).deleteById(1L);
    }

    @Test
    void testExistsByName_DelegatesToRepository() {
        when(irepositoryproduct.existsByNameIgnoreCase("Laptop ESPRIT")).thenReturn(true);

        assertTrue(productService.existsByName("Laptop ESPRIT"));
        verify(irepositoryproduct).existsByNameIgnoreCase("Laptop ESPRIT");
    }

    @Test
    void testExistsByName_BlankReturnsFalse() {
        assertFalse(productService.existsByName("   "));
        verify(irepositoryproduct, never()).existsByNameIgnoreCase(any());
    }
}
