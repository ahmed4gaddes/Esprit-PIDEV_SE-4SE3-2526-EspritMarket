package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.entity.ProductImage;
import tn.esprit.esprit_market.modules.store.service.IServiceProcuctImage;

import java.util.List;

@RestController
@RequestMapping("ProductImage")
@AllArgsConstructor
public class RestControllerProductImage {
    private IServiceProcuctImage iServiceProcuctImage;
    @PostMapping("addproductimage")
    public ProductImage addProductImage( @RequestBody ProductImage productImage) {
        return iServiceProcuctImage.addProductImage(productImage);
    }

    @PutMapping("updateProductImage")
    public ProductImage updateProductImage( @RequestBody ProductImage productImage) {
        return iServiceProcuctImage.updateProductImage(productImage);
    }

    @GetMapping("get/{id}")
    public ProductImage getById( @PathVariable Long id) {
        return iServiceProcuctImage.getById(id);
    }

    @GetMapping("getAll")
    public List<ProductImage> getAllProductImage() {
        return iServiceProcuctImage.getAllProductImage();
    }

    @DeleteMapping("delete")
    public void deleteProductImage(@RequestBody ProductImage productImage) {
       iServiceProcuctImage.deleteProductImage(productImage);

    }
}
