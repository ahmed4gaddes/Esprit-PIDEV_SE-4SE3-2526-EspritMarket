package tn.esprit.esprit_market.modules.store.service;

import tn.esprit.esprit_market.modules.store.entity.ProductImage;

import java.util.List;

public interface IServiceProcuctImage {
    ProductImage addProductImage(ProductImage productImage);
    ProductImage updateProductImage(ProductImage productImage);
    ProductImage getById(Long id);
     List<ProductImage> getAllProductImage();
    void deleteProductImage(Long id);
}
