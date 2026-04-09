package tn.esprit.esprit_market.modules.store.service;

import tn.esprit.esprit_market.modules.store.dto.ProductDTO;
import tn.esprit.esprit_market.modules.store.entity.Product;

import java.util.List;

public interface IproductService {
    Product addProduct( Product product);
    Product updateProduct(Product product , Long id );
    Product getProductById(Long id);
    List<Product> getAllProducts();
    void deleteProduct(Long id);
    List<ProductDTO> searchProducts(String name, Double minPrice, Double maxPrice, Long categoryId);}
