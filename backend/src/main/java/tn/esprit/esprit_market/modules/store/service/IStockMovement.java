package tn.esprit.esprit_market.modules.store.service;

import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;

import java.util.List;

public interface IStockMovement {
    StockMovement addStock(StockMovement stockMovement);
    StockMovement getStockById(Long id);
    List<StockMovement> getAllStock();
    StockMovement updateStock(StockMovement stockMovement, Long id);
    void deleteStock(Long id);
    Product decrementStock(Long productId, int quantity);
    Product incrementStock(Long id, int quantity);
    List<Product> getOutOfStockProducts();
    List<Product> getLowStockProducts();
}
