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
import tn.esprit.esprit_market.modules.store.repository.StockAlertRepository;
import tn.esprit.esprit_market.modules.store.entity.StockAlert;
import tn.esprit.esprit_market.modules.store.enums.StockStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

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
    private StockAlertRepository stockAlertRepository;
    private JavaMailSender mailSender;
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

        boolean wasOutOfStock = existing.getStock() == 0 || existing.getStockStatus() == StockStatus.OUT_OF_STOCK;

        // Mettre à jour les champs
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        existing.setActive(product.isActive());
        existing.setStore(product.getStore());
        existing.setCategory(product.getCategory());

        // Update Stock Status explicitly
        if (existing.getStock() == 0) {
            existing.setStockStatus(StockStatus.OUT_OF_STOCK);
        } else if (existing.getStock() <= existing.getStockThreshold()) {
            existing.setStockStatus(StockStatus.LOW_STOCK);
        } else {
            existing.setStockStatus(StockStatus.IN_STOCK);
        }

        //Sauvegarder
        Product saved = irepositoryproduct.save(existing);

        if (wasOutOfStock && saved.getStock() > 0) {
            notifySubscribersBackInStock(saved);
        }

        return saved;
    }

    private void notifySubscribersBackInStock(Product product) {
        List<StockAlert> alerts = stockAlertRepository.findByProductIdAndNotifiedFalse(product.getId());
        for (StockAlert alert : alerts) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("guesmiahlem365@gmail.com");
            message.setTo(alert.getUser().getEmail());
            message.setSubject("🎉 Retour en stock : " + product.getName());
            message.setText("Bonjour " + alert.getUser().getName() + ",\n\n" +
                    "Bonne nouvelle ! Le produit '" + product.getName() + "' est de nouveau disponible.\n" +
                    "Ne tardez pas, le stock est limité !\n\n" +
                    "L'équipe EspritMarket.");
            try {
                mailSender.send(message);
                alert.setNotified(true);
                stockAlertRepository.save(alert);
            } catch (Exception e) {
                System.err.println("Erreur d'envoi d'email à " + alert.getUser().getEmail());
            }
        }
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
                // ✅ Calculé dynamiquement depuis le stock réel
                .stockStatus(resolveStockStatus(p))
                .build()
        ).collect(Collectors.toList());
    }

    /** Calcule le StockStatus depuis le stock réel (corrige les anciens produits en BD) */
    private StockStatus resolveStockStatus(Product p) {
        if (p.getStock() == 0)                          return StockStatus.OUT_OF_STOCK;
        if (p.getStock() <= p.getStockThreshold())      return StockStatus.LOW_STOCK;
        return StockStatus.IN_STOCK;
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
