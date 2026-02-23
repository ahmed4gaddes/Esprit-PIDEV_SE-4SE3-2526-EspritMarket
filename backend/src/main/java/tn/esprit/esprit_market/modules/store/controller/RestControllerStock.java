package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;
import tn.esprit.esprit_market.modules.store.service.IStockMovement;

import java.util.List;
@RestController
@RequestMapping("Stock")
@AllArgsConstructor
public class RestControllerStock {
    private IStockMovement iStockMovement;
    @PostMapping("addstock")
    public StockMovement addStock( @RequestBody StockMovement stockMovement) {
        return  iStockMovement.addStock(stockMovement);
    }

  @GetMapping("get/{id}")
    public StockMovement getStockById( @PathVariable Long id) {
        return iStockMovement.getStockById(id);
    }

    @GetMapping("getall")
    public List<StockMovement> getAllStock() {
        return iStockMovement.getAllStock();
    }

    @PutMapping("update")
    public StockMovement updateStock(@RequestBody StockMovement stockMovement) {
        return iStockMovement.updateStock(stockMovement);
    }

    @DeleteMapping("delete")
    public void deleteStock(StockMovement stockMovement) {
       iStockMovement.deleteStock(stockMovement);
    }
}
