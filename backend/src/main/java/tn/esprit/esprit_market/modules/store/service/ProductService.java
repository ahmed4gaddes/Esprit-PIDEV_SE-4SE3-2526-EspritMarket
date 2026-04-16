package tn.esprit.esprit_market.modules.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.dto.ProductDTO;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryCategory;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStockMovement;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService implements IproductService {
    private IRepositoryProduct irepositoryproduct;
    private IRepositoryStore iRepositoryStore;
    private IRepositoryCategory iRepositoryCategory;
    private IRepositoryStockMovement iRepositoryStockMovement;
    @Override
    public Product addProduct(Product product) {
        Store store = iRepositoryStore.findById(product.getStore().getId())
                .orElseThrow(() -> new EntityNotFoundException("Store introuvable"));

            Category category = iRepositoryCategory.findById(product.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Category introuvable"));
        product.setStore(store);
        product.setCategory(category);
        return  irepositoryproduct.save(product);
    }

    @Override
    public Product updateProduct(Product product, Long id) {
        //  Vérifier que le produit existe
        Product existing = irepositoryproduct.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec id: " + id));

        // Mettre à jour les champs
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        existing.setActive(product.isActive());
        existing.setStore(product.getStore());
        existing.setCategory(product.getCategory());

        //Sauvegarder
        return irepositoryproduct.save(existing);
    }

    @Override
    public Product getProductById(Long id) {
        return irepositoryproduct.findById(id).orElseThrow(() -> new NoSuchElementException("Prodcut not found with id: " + id));
    }

    @Override
    public  List<Product> getAllProducts() {
        return irepositoryproduct.findAll();
    }


    @Override
    public List<ProductDTO> searchProducts(String name, Double minPrice, Double maxPrice, Long categoryId) {
        List<Product> products = irepositoryproduct.searchProducts(name, minPrice, maxPrice, categoryId);
        return products.stream().map(p -> ProductDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .active(p.isActive())
                .createdAt(p.getCreatedAt())
                .storeId(p.getStore() != null ? p.getStore().getId() : null)
                .storeName(p.getStore() != null ? p.getStore().getName() : null)
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .imageUrl(p.getImageUrl())
                .build()
        ).collect(Collectors.toList());
    }
    @Override
    public void deleteProduct(Long id) {
         irepositoryproduct.deleteById(id); }
//    @Scheduled(cron = "*/10 3 * * * *")
//    public void deactivateOutOfStockProducts() {
//        // Récupère tous les produits actifs
//        List<Product> activeProducts = irepositoryproduct.findByActiveTrue();
//
//        for (Product product : activeProducts) {
//            // Calcule le stock total pour ce produit (sur tous les stores)
//            int totalStock = iRepositoryStockMovement.sumQuantityByProduct(product.getId());
//
//            if (totalStock == 0) {
//                product.setActive(false);
//                irepositoryproduct.save(product); // mise à jour BDD
//            }
//        }
//        System.out.println("Produits sans stock désactivés à " + LocalDateTime.now());
//    }
}
