package tn.esprit.esprit_market.modules.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.StockAlert;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;
import tn.esprit.esprit_market.modules.store.enums.MovementType;
import tn.esprit.esprit_market.modules.store.enums.StockStatus;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStockMovement;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class StockMovementService implements IStockMovement {
    private IRepositoryStockMovement iRepositoryStockMovement;
    private IRepositoryProduct iRepositoryProduct;
    private tn.esprit.esprit_market.modules.store.repository.StockAlertRepository stockAlertRepository; // ✅ Ajouter repo StockAlert
    private JavaMailSender mailSender; // ✅ Ajouter cette ligne

    @Override
    public StockMovement addStock(StockMovement stockMovement) {
        Product product = iRepositoryProduct.findById(stockMovement.getProduct().getId())
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));

        if (stockMovement.getType() == MovementType.IN) {
            product.setStock(product.getStock() + stockMovement.getQuantity());
        } else if (stockMovement.getType() == MovementType.OUT) {
            // Le module Order (OrderServiceImpl) décrémente déjà le stock du produit.
            // Pour éviter que le produit soit décrémenté par 2, on vérifie si l'appel vient de là.
            boolean isFromOrder = java.util.Arrays.stream(Thread.currentThread().getStackTrace())
                    .anyMatch(element -> element.getClassName().contains("OrderServiceImpl"));

            if (!isFromOrder) {
                if (product.getStock() < stockMovement.getQuantity()) {
                    throw new RuntimeException("Stock insuffisant");
                }
                product.setStock(product.getStock() - stockMovement.getQuantity());
            } else {
                if (product.getStock() < 0) {
                    throw new RuntimeException("Stock insuffisant pour la commande");
                }
            }
        }
        
        boolean wasOutOfStock = product.getStockStatus() == StockStatus.OUT_OF_STOCK;

        updateStockStatus(product);
        iRepositoryProduct.save(product);

        if (wasOutOfStock && product.getStock() > 0) {
            notifySubscribersBackInStock(product);
        }

        stockMovement.setProduct(product);
        return iRepositoryStockMovement.save(stockMovement);
    }

    @Override
    public StockMovement updateStock(StockMovement stockMovement, Long id) {
        StockMovement existing = iRepositoryStockMovement.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock introuvable avec id: " + id));

        Product product = iRepositoryProduct.findById(stockMovement.getProduct().getId())
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));

        // Revert old stock
        if (existing.getType() == MovementType.IN) {
            product.setStock(product.getStock() - existing.getQuantity());
        } else if (existing.getType() == MovementType.OUT) {
            product.setStock(product.getStock() + existing.getQuantity());
        }

        // Apply new stock
        if (stockMovement.getType() == MovementType.IN) {
            product.setStock(product.getStock() + stockMovement.getQuantity());
        } else if (stockMovement.getType() == MovementType.OUT) {
            if (product.getStock() < stockMovement.getQuantity()) {
                throw new RuntimeException("Stock insuffisant pour cette mise à jour");
            }
            product.setStock(product.getStock() - stockMovement.getQuantity());
        }

        existing.setQuantity(stockMovement.getQuantity());
        existing.setType(stockMovement.getType());
        existing.setDate(stockMovement.getDate());
        existing.setProduct(product);

        boolean wasOutOfStock = product.getStockStatus() == StockStatus.OUT_OF_STOCK;

        updateStockStatus(product);
        iRepositoryProduct.save(product);

        if (wasOutOfStock && product.getStock() > 0) {
            notifySubscribersBackInStock(product);
        }

        return iRepositoryStockMovement.save(existing);
    }

    @Override
    public StockMovement getStockById(Long id) {
        return iRepositoryStockMovement.findById(id).orElseThrow(() -> new NoSuchElementException("StockMovement not found with id: " + id));
    }

    @Override
    public List<StockMovement> getAllStock() {
        return iRepositoryStockMovement.findAll();
    }



    @Override
    public void deleteStock(Long id) {
        StockMovement existing = iRepositoryStockMovement.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock introuvable avec id: " + id));
        Product product = existing.getProduct();

        // Revert stock
        if (existing.getType() == MovementType.IN) {
            product.setStock(product.getStock() - existing.getQuantity());
        } else if (existing.getType() == MovementType.OUT) {
            product.setStock(product.getStock() + existing.getQuantity());
        }
        
        updateStockStatus(product);
        iRepositoryProduct.save(product);

        iRepositoryStockMovement.deleteById(id);
    }


    @Override
    public Product decrementStock(Long productId, int quantity) {
        Product product = iRepositoryProduct.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuffisant : " + product.getStock() + " disponible");
        }

        product.setStock(product.getStock() - quantity);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .quantity(quantity)
                .type(MovementType.OUT)
                .date(new Date())
                .build();
        iRepositoryStockMovement.save(movement);

        updateStockStatus(product);
        return iRepositoryProduct.save(product);
    }

    @Override
    public Product incrementStock(Long id, int quantity) {
        Product product = iRepositoryProduct.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStock(product.getStock() + quantity);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .quantity(quantity)
                .type(MovementType.IN)
                .date(new Date())
                .build();
        iRepositoryStockMovement.save(movement);

        boolean wasOutOfStock = product.getStockStatus() == StockStatus.OUT_OF_STOCK;

        updateStockStatus(product);
        
        if (wasOutOfStock && product.getStock() > 0) {
            notifySubscribersBackInStock(product);
        }
        return iRepositoryProduct.save(product);
    }

    @Override
    public List<Product> getOutOfStockProducts() {
        return iRepositoryProduct.findByStockStatus(StockStatus.OUT_OF_STOCK);
    }

    @Override
    public List<Product> getLowStockProducts() {
        return iRepositoryProduct.findByStockStatus(StockStatus.LOW_STOCK);
    }

    private void updateStockStatus(Product product) {
        StockStatus oldStatus = product.getStockStatus();

        if (product.getStock() == 0) {
            product.setStockStatus(StockStatus.OUT_OF_STOCK);
            sendStockAlert(product, "RUPTURE DE STOCK");
        } else if (product.getStock() <= product.getStockThreshold()) {
            product.setStockStatus(StockStatus.LOW_STOCK);
            sendStockAlert(product, "STOCK FAIBLE");
        } else {
            product.setStockStatus(StockStatus.IN_STOCK);
        }
    }

    private void sendStockAlert(Product product, String alertType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("guesmiahlem365@gmail.com"); // Requis par Gmail
        message.setTo("guesmiahlem365@gmail.com");
        message.setSubject("⚠️ Alerte Stock — " + alertType);
        message.setText(
                "Produit  : " + product.getName() + "\n" +
                        "Stock    : " + product.getStock() + " unités\n" +
                        "Seuil    : " + product.getStockThreshold() + " unités\n" +
                        "Statut   : " + alertType + "\n" +
                        "Date     : " + new Date()
        );
        mailSender.send(message); //
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
}
