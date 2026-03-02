
package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.dto.StockMovementDTO;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;
import tn.esprit.esprit_market.modules.store.mapper.StockMovementMapper;
import tn.esprit.esprit_market.modules.store.service.IStockMovement;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("Stock")
@AllArgsConstructor
public class RestControllerStock {
    private IStockMovement iStockMovement;
    private StockMovementMapper stockMovementMapper;
    @PostMapping("addstock")
    public StockMovementDTO addStock(@RequestBody StockMovementDTO dto) {
        StockMovement stockMovement = new StockMovement();
        stockMovement.setQuantity(dto.getQuantity());
        stockMovement.setType(dto.getType());
        stockMovement.setDate(new Date());  // ✅ date automatique

        Product product = new Product();
        product.setId(dto.getProductId());
        stockMovement.setProduct(product);

        StockMovement saved = iStockMovement.addStock(stockMovement);
        StockMovement full  = iStockMovement.getStockById(saved.getId());
        return stockMovementMapper.toDTO(full);  // ✅ retourne DTO
    }

    @PutMapping("update/{id}")
    public StockMovementDTO updateStock(@PathVariable Long id, @RequestBody StockMovementDTO dto) {
        StockMovement stockMovement = new StockMovement();
        stockMovement.setQuantity(dto.getQuantity());
        stockMovement.setType(dto.getType());
        stockMovement.setDate(new Date());  // ✅ date mise à jour

        Product product = new Product();
        product.setId(dto.getProductId());
        stockMovement.setProduct(product);

        StockMovement updated = iStockMovement.updateStock(stockMovement, id);
        StockMovement full    = iStockMovement.getStockById(updated.getId());
        return stockMovementMapper.toDTO(full);  // ✅ retourne DTO
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


    @DeleteMapping("delete/{id}")
    public void deleteStock( @PathVariable Long id) {
       iStockMovement.deleteStock(id);
    }
}
