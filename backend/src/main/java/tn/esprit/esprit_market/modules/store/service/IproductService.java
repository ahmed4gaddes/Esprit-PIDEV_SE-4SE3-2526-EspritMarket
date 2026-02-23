package tn.esprit.esprit_market.modules.store.service;

import tn.esprit.esprit_market.modules.store.entity.Product;

import java.util.List;

public interface IproductService {
    Product addProduct( Product product);
    Product updateProduct(Product product );
    Product getProductById(Long id);
    List<Product> getAllProducts();
    void deleteProduct(Product product);
}
