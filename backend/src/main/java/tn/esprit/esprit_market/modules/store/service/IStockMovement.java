package tn.esprit.esprit_market.modules.store.service;

import tn.esprit.esprit_market.modules.store.entity.StockMovement;

import java.util.List;

public interface IStockMovement {
    StockMovement addStock(StockMovement stockMovement);
    StockMovement getStockById(Long id);
    List<StockMovement> getAllStock();
    StockMovement updateStock(StockMovement stockMovement);
    void deleteStock( StockMovement stockMovement );
}
