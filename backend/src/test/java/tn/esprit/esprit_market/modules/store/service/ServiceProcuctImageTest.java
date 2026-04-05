package tn.esprit.esprit_market.modules.store.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.ProductImage;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProductImage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceProcuctImageTest {

    @Mock
    private IRepositoryProductImage iRepositoryProductImage;

    @Mock
    private IRepositoryProduct iRepositoryProduct;

    @InjectMocks
    private ServiceProcuctImage serviceProcuctImage;

    private ProductImage fakeImage;
    private Product fakeProduct;

    @BeforeEach
    void setUp() {
        fakeProduct = new Product();
        fakeProduct.setId(1L);
        fakeProduct.setName("Laptop ESPRIT");

        fakeImage = new ProductImage();
        fakeImage.setId(1L);
        fakeImage.setUrl("https://esprit.tn/images/laptop.jpg");
        fakeImage.setAltText("Photo du Laptop");
        fakeImage.setImageOrder(1);
        fakeImage.setProduct(fakeProduct);
    }

    // ==================== addProductImage ====================
    @Test
    void testAddProductImage_Success() {
        when(iRepositoryProduct.findById(1L)).thenReturn(Optional.of(fakeProduct));
        when(iRepositoryProductImage.save(any(ProductImage.class))).thenReturn(fakeImage);

        ProductImage result = serviceProcuctImage.addProductImage(fakeImage);

        assertNotNull(result);
        assertEquals("https://esprit.tn/images/laptop.jpg", result.getUrl());
        assertEquals("Photo du Laptop", result.getAltText());
        verify(iRepositoryProductImage, times(1)).save(any(ProductImage.class));
    }

    @Test
    void testAddProductImage_ProductNotFound_ThrowsException() {
        when(iRepositoryProduct.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> serviceProcuctImage.addProductImage(fakeImage));
        verify(iRepositoryProductImage, never()).save(any());
    }

    // ==================== getProductImageById ====================
    @Test
    void testGetProductImageById_Found() {
        when(iRepositoryProductImage.findById(1L)).thenReturn(Optional.of(fakeImage));

        ProductImage result = serviceProcuctImage.getProductImageById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetProductImageById_NotFound_ThrowsException() {
        when(iRepositoryProductImage.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> serviceProcuctImage.getProductImageById(99L));
    }

    // ==================== getAllProductImage ====================
    @Test
    void testGetAllProductImage_ReturnsList() {
        ProductImage img2 = new ProductImage();
        img2.setId(2L);
        img2.setUrl("https://esprit.tn/images/laptop2.jpg");
        when(iRepositoryProductImage.findAll()).thenReturn(Arrays.asList(fakeImage, img2));

        List<ProductImage> result = serviceProcuctImage.getAllProductImage();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ==================== updateProductImage ====================
    @Test
    void testUpdateProductImage_Success() {
        ProductImage updatedData = new ProductImage();
        updatedData.setUrl("https://esprit.tn/images/new.jpg");
        updatedData.setAltText("Nouvelle photo");
        updatedData.setImageOrder(2);
        updatedData.setProduct(fakeProduct);

        when(iRepositoryProductImage.findById(1L)).thenReturn(Optional.of(fakeImage));
        when(iRepositoryProduct.findById(1L)).thenReturn(Optional.of(fakeProduct));
        when(iRepositoryProductImage.save(any(ProductImage.class))).thenReturn(fakeImage);

        ProductImage result = serviceProcuctImage.updateProductImage(updatedData, 1L);

        assertNotNull(result);
        verify(iRepositoryProductImage, times(1)).save(any(ProductImage.class));
    }

    @Test
    void testUpdateProductImage_NotFound_ThrowsException() {
        when(iRepositoryProductImage.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> serviceProcuctImage.updateProductImage(new ProductImage(), 99L));
    }

    // ==================== deleteProductImage ====================
    @Test
    void testDeleteProductImage_Success() {
        doNothing().when(iRepositoryProductImage).deleteById(1L);

        serviceProcuctImage.deleteProductImage(1L);

        verify(iRepositoryProductImage, times(1)).deleteById(1L);
    }
}
