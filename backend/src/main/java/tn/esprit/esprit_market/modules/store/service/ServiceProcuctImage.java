package tn.esprit.esprit_market.modules.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.ProductImage;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProductImage;

import java.util.List;

@Service
@AllArgsConstructor

public class ServiceProcuctImage implements IServiceProcuctImage {
    private IRepositoryProductImage iRepositoryProductImage;
    private IRepositoryProduct iRepositoryProduct;
    @Override
    public ProductImage addProductImage(ProductImage productImage) {
        Product product = iRepositoryProduct.findById(productImage.getProduct().getId())
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));
        productImage.setProduct(product);
        return iRepositoryProductImage.save(productImage);
    }
    @Override
    public ProductImage getProductImageById(Long id) {
        return iRepositoryProductImage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image introuvable avec id: " + id));
    }
    @Override
    public ProductImage updateProductImage(ProductImage productImage, Long id) {
        ProductImage existing = iRepositoryProductImage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image introuvable avec id: " + id));

        existing.setUrl(productImage.getUrl());
        existing.setAltText(productImage.getAltText());
        existing.setImageOrder(productImage.getImageOrder());

        Product product = iRepositoryProduct.findById(productImage.getProduct().getId())
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));
        existing.setProduct(product);

        return iRepositoryProductImage.save(existing);
    }

    @Override
    public ProductImage getById(Long id) {
        return iRepositoryProductImage.findById(id).get();
    }

    @Override
    public List<ProductImage> getAllProductImage() {
        return iRepositoryProductImage.findAll();
    }

    @Override
    public void deleteProductImage(Long id) {
        iRepositoryProductImage.deleteById(id);

    }
}
