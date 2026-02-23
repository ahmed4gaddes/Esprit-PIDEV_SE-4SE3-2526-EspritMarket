package tn.esprit.esprit_market.modules.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStockMovement;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class StockMovementService implements IStockMovement {
    private IRepositoryStockMovement iRepositoryStockMovement;
    private IRepositoryProduct iRepositoryProduct;
    @Override
    public StockMovement addStock(StockMovement stockMovement) {
        Product product = iRepositoryProduct.findById(stockMovement.getProduct().getId())
                .orElseThrow(() -> new EntityNotFoundException("product introuvable"));
        stockMovement.setProduct(product);
        return  iRepositoryStockMovement.save(stockMovement);
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
    public StockMovement updateStock( StockMovement stockMovement) {
        return iRepositoryStockMovement.save(stockMovement);
    }

    @Override
    public void deleteStock(StockMovement stockMovement) {
 iRepositoryStockMovement.delete(stockMovement);
    }
}
