package tn.esprit.esprit_market.modules.store.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.store.dto.ProductImageDTO;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.ProductImage;
import tn.esprit.esprit_market.modules.store.mapper.ProductImageMapper;
import tn.esprit.esprit_market.modules.store.service.IServiceProcuctImage;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestControllerProductImageTest {

    @Mock
    private IServiceProcuctImage iServiceProcuctImage;

    @Mock
    private ProductImageMapper productImageMapper;

    @InjectMocks
    private RestControllerProductImage controller;

    private ProductImage productImage;
    private ProductImageDTO productImageDTO;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setId(1L);

        productImage = new ProductImage();
        productImage.setId(1L);
        productImage.setUrl("http://img.com/1.jpg");
        productImage.setAltText("Image 1");
        productImage.setImageOrder(1);
        productImage.setProduct(product);

        productImageDTO = new ProductImageDTO();
        productImageDTO.setId(1L);
        productImageDTO.setUrl("http://img.com/1.jpg");
        productImageDTO.setAltText("Image 1");
        productImageDTO.setOrder(1);
        productImageDTO.setProductId(1L);
    }

    @Test
    void testAddProductImage() {
        when(iServiceProcuctImage.addProductImage(any(ProductImage.class))).thenReturn(productImage);
        when(iServiceProcuctImage.getProductImageById(1L)).thenReturn(productImage);
        when(productImageMapper.toDTO(productImage)).thenReturn(productImageDTO);

        ProductImageDTO result = controller.addProductImage(productImageDTO);

        assertNotNull(result);
        assertEquals("http://img.com/1.jpg", result.getUrl());
        verify(iServiceProcuctImage).addProductImage(any(ProductImage.class));
    }

    @Test
    void testUpdateProductImage() {
        when(iServiceProcuctImage.updateProductImage(any(ProductImage.class), eq(1L))).thenReturn(productImage);
        when(iServiceProcuctImage.getProductImageById(1L)).thenReturn(productImage);
        when(productImageMapper.toDTO(productImage)).thenReturn(productImageDTO);

        ProductImageDTO result = controller.updateProductImage(1L, productImageDTO);

        assertNotNull(result);
        verify(iServiceProcuctImage).updateProductImage(any(ProductImage.class), eq(1L));
    }

    @Test
    void testGetById() {
        when(iServiceProcuctImage.getById(1L)).thenReturn(productImage);
        when(productImageMapper.toDTO(productImage)).thenReturn(productImageDTO);

        ProductImageDTO result = controller.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetAllProductImage() {
        when(iServiceProcuctImage.getAllProductImage()).thenReturn(Arrays.asList(productImage));
        when(productImageMapper.toDTO(productImage)).thenReturn(productImageDTO);

        List<ProductImageDTO> results = controller.getAllProductImage();

        assertEquals(1, results.size());
    }

    @Test
    void testDeleteProductImage() {
        doNothing().when(iServiceProcuctImage).deleteProductImage(1L);

        controller.deleteProductImage(1L);

        verify(iServiceProcuctImage).deleteProductImage(1L);
    }
}
