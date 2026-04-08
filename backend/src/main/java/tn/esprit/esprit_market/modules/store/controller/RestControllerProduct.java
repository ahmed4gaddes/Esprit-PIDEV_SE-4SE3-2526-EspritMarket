package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.dto.ProductDTO;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.mapper.ProductMapper;
import tn.esprit.esprit_market.modules.store.service.IproductService;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("Product")
@RestController
@AllArgsConstructor
public class RestControllerProduct {
    private IproductService iproductService;
    private ProductMapper productMapper;      // ✅ Ajouter le Mapper

    @PostMapping("addproduct")
    public ProductDTO addProduct(@RequestBody ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setActive(dto.isActive());
        Store store = new Store();
        store.setId(dto.getStoreId());
        product.setStore(store);
        Category category = new Category();
        category.setId(dto.getCategoryId());
        product.setCategory(category);
        product.setImageUrl(dto.getImageUrl());

        // ✅ Sauvegarder puis recharger avec les relations complètes
        Product saved = iproductService.addProduct(product);
        Product full  = iproductService.getProductById(saved.getId());
        return productMapper.toDTO(full);
    }

    @PutMapping("update/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setActive(dto.isActive());

        Store store = new Store();
        store.setId(dto.getStoreId());
        product.setStore(store);
        Category category = new Category();
        category.setId(dto.getCategoryId());
        product.setCategory(category);
        product.setImageUrl(dto.getImageUrl());

        // ✅ Passer l'id au service
        Product updated = iproductService.updateProduct(product, id);
        Product full    = iproductService.getProductById(updated.getId());
        return productMapper.toDTO(full);
    }
    @DeleteMapping("delete/{id}")
    public void deleteProduct(@PathVariable Long id) {
        iproductService.deleteProduct(id);
    }

    // ✅ GET par id → retourne DTO
    @GetMapping("get/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        Product product = iproductService.getProductById(id);
        return productMapper.toDTO(product);  // ✅ toDTO ici
    }

    // ✅ GET all → retourne DTO
    @GetMapping("getall")
    public List<ProductDTO> getAllProducts() {
        return iproductService.getAllProducts()
                .stream()
                .map(productMapper::toDTO)    // ✅ toDTO ici
                .collect(Collectors.toList());
    }

    // ✅ GET search → retourne DTO
    @GetMapping("search")
    public List<ProductDTO> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        return iproductService.searchProducts(name, categoryId, minPrice, maxPrice)
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }
}
