package tn.esprit.esprit_market.modules.store.controller;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.service.MLService;
import tn.esprit.esprit_market.modules.store.service.IproductService;
import tn.esprit.esprit_market.modules.store.mapper.ProductMapper;
import tn.esprit.esprit_market.modules.store.dto.ProductDTO;
import tn.esprit.esprit_market.modules.store.entity.Product;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
@AllArgsConstructor
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class SearchController {


    private MLService mlService;
    private IproductService iproductService;
    private ProductMapper productMapper;

    @PostMapping("/search")
    public ResponseEntity<List<ProductDTO>> search(
            @RequestBody Map<String, String> body) {

        String query = body.get("query");
        
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(iproductService.getAllProducts()
                    .stream()
                    .map(productMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        List<Map<String, Object>> results = null;
        try {
            results = mlService.search(query);
        } catch (Exception e) {
            // Python API is offline or failed
            System.err.println("Notice: ML Service offline or failed. Falling back to keyword search.");
        }
        
        List<Product> allProducts = iproductService.getAllProducts();
        List<ProductDTO> matchedDtoList = new ArrayList<>();
        
        if (results != null && !results.isEmpty()) {
            for (Map<String, Object> map : results) {
                String name = (String) map.get("name");
                boolean found = false;
                for (Product p : allProducts) {
                    String pName = p.getName() != null ? p.getName().toLowerCase() : "";
                    String aiName = name != null ? name.toLowerCase() : "";
                    if (!pName.isEmpty() && !aiName.isEmpty() && 
                        (pName.contains(aiName) || aiName.contains(pName))) {
                        matchedDtoList.add(productMapper.toDTO(p));
                        found = true;
                        break;
                    }
                }
                
                // If the product from the AI model (from CSV) isn't in the live database,
                // we still want to show the AI's recommendation to the user!
                if (!found && name != null) {
                    ProductDTO dto = new ProductDTO();
                    dto.setId(0L); // 0 or null ID will disable 'Add to cart' gracefully
                    dto.setName(name);
                    
                    Object priceObj = map.get("price");
                    if (priceObj instanceof Number) {
                         dto.setPrice(((Number) priceObj).floatValue());
                    }
                    
                    dto.setDescription((String) map.get("description"));
                    matchedDtoList.add(dto);
                }
            }
        } else {
            // Fallback: simple keyword/category search simulating AI
            String[] keywords = query.toLowerCase().split("\\s+");
            for (Product p : allProducts) {
                String pName = p.getName() != null ? p.getName().toLowerCase() : "";
                String pDesc = p.getDescription() != null ? p.getDescription().toLowerCase() : "";
                String pCat = (p.getCategory() != null && p.getCategory().getName() != null) 
                               ? p.getCategory().getName().toLowerCase() : "";

                boolean matches = false;
                for (String kw : keywords) {
                    if (kw.length() >= 2 && (pName.contains(kw) || pDesc.contains(kw) || pCat.contains(kw))) {
                        matches = true;
                        break;
                    }
                }
                // Check exact query strings directly
                if (!matches && (pName.contains(query.toLowerCase()) || pDesc.contains(query.toLowerCase()))) {
                    matches = true;
                }
                
                if (matches) {
                    matchedDtoList.add(productMapper.toDTO(p));
                }
            }
        }
        
        return ResponseEntity.ok(matchedDtoList);
    }
}