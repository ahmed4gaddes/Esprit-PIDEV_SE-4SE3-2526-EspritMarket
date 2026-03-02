

package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.dto.ProductImageDTO;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.ProductImage;
import tn.esprit.esprit_market.modules.store.mapper.ProductImageMapper;
import tn.esprit.esprit_market.modules.store.service.IServiceProcuctImage;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("ProductImage")
@AllArgsConstructor
public class RestControllerProductImage {
    private IServiceProcuctImage iServiceProcuctImage;
    private ProductImageMapper productImageMapper;  // ✅ Ajouter mapper

    @PostMapping("addproductimage")
    public ProductImageDTO addProductImage(@RequestBody ProductImageDTO dto) {
        ProductImage productImage = new ProductImage();
        productImage.setUrl(dto.getUrl());
        productImage.setAltText(dto.getAltText());
        productImage.setImageOrder(dto.getOrder());

        Product product = new Product();
        product.setId(dto.getProductId());
        productImage.setProduct(product);

        ProductImage saved = iServiceProcuctImage.addProductImage(productImage);
        ProductImage full  = iServiceProcuctImage.getProductImageById(saved.getId());
        return productImageMapper.toDTO(full);
    }

    @PutMapping("updateProductImage/{id}")
    public ProductImageDTO updateProductImage(@PathVariable Long id, @RequestBody ProductImageDTO dto) {
        ProductImage productImage = new ProductImage();
        productImage.setUrl(dto.getUrl());
        productImage.setAltText(dto.getAltText());
        productImage.setImageOrder(dto.getOrder());

        Product product = new Product();
        product.setId(dto.getProductId());
        productImage.setProduct(product);

        ProductImage updated = iServiceProcuctImage.updateProductImage(productImage, id);
        ProductImage full    = iServiceProcuctImage.getProductImageById(updated.getId());
        return productImageMapper.toDTO(full);
    }

    @GetMapping("get/{id}")
    public ProductImageDTO getById(@PathVariable Long id) {
        ProductImage image = iServiceProcuctImage.getById(id);
        return productImageMapper.toDTO(image);  // ✅ toDTO
    }

    // ✅ GET all → retourne DTO
    @GetMapping("getAll")
    public List<ProductImageDTO> getAllProductImage() {
        return iServiceProcuctImage.getAllProductImage()
                .stream()
                .map(productImageMapper::toDTO)  // ✅ toDTO
                .collect(Collectors.toList());
    }
    @DeleteMapping("delete/{id}")
    public void deleteProductImage(@PathVariable Long id) {
       iServiceProcuctImage.deleteProductImage(id);

    }
}
