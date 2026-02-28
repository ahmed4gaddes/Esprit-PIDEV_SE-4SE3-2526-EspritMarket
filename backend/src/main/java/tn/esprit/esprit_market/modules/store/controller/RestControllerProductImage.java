

package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.dto.ProductImageDTO;
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
    public ProductImage addProductImage( @RequestBody ProductImage productImage) {
        return iServiceProcuctImage.addProductImage(productImage);
    }

    @PutMapping("updateProductImage")
    public ProductImage updateProductImage( @RequestBody ProductImage productImage) {
        return iServiceProcuctImage.updateProductImage(productImage);
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
