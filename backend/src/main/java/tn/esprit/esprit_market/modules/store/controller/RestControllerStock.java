
package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.dto.StockMovementDTO;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;
import tn.esprit.esprit_market.modules.store.mapper.StockMovementMapper;
import tn.esprit.esprit_market.modules.store.service.IStockMovement;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("Stock")
@AllArgsConstructor
public class RestControllerStock {
    private IStockMovement iStockMovement;
    private StockMovementMapper stockMovementMapper;
    @PostMapping("addstock")
    public StockMovement addStock( @RequestBody StockMovement stockMovement) {
        return  iStockMovement.addStock(stockMovement);
    }

    @GetMapping("get/{id}")
    public StockMovementDTO getStockById(@PathVariable Long id) {
        StockMovement stock = iStockMovement.getStockById(id);
        return stockMovementMapper.toDTO(stock);  // ✅ toDTO
    }

    // ✅ GET all → retourne DTO
    @GetMapping("getall")
    public List<StockMovementDTO> getAllStock() {
        return iStockMovement.getAllStock()
                .stream()
                .map(stockMovementMapper::toDTO)  // ✅ toDTO
                .collect(Collectors.toList());
    }

    @PutMapping("update")
    public StockMovement updateStock(@RequestBody StockMovement stockMovement) {
        return iStockMovement.updateStock(stockMovement);
    }

    @DeleteMapping("delete/{id}")
    public void deleteStock( @PathVariable Long id) {
       iStockMovement.deleteStock(id);
    }
}
