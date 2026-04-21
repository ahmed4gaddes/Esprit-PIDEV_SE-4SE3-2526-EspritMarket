//package tn.esprit.esprit_market.modules.store.controller;
//
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import tn.esprit.esprit_market.modules.store.service.MLService;
//import tn.esprit.esprit_market.modules.store.service.IproductService;
//import tn.esprit.esprit_market.modules.store.mapper.ProductMapper;
//import tn.esprit.esprit_market.modules.store.dto.ProductDTO;
//import tn.esprit.esprit_market.modules.store.entity.Product;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@AllArgsConstructor
//@RestController
//@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:4200")
//public class SearchController {
//
//    private MLService mlService;
//    private IproductService iproductService;
//    private ProductMapper productMapper;
//
//    // ── /api/search  (recherche classique avec fallback keyword) ─
//    @PostMapping("/search")
//    public ResponseEntity<List<ProductDTO>> search(
//            @RequestBody Map<String, String> body) {
//
//        String query = body.get("query");
//
//        if (query == null || query.trim().isEmpty()) {
//            return ResponseEntity.ok(
//                    iproductService.getAllProducts()
//                            .stream()
//                            .map(productMapper::toDTO)
//                            .collect(Collectors.toList())
//            );
//        }
//
//        List<Map<String, Object>> results = null;
//        try {
//            results = mlService.search(query);
//        } catch (Exception e) {
//            System.err.println("Notice: ML Service offline. Falling back to keyword search.");
//        }
//
//        List<Product> allProducts = iproductService.getAllProducts();
//
//        if (results != null && !results.isEmpty()) {
//            return ResponseEntity.ok(matchProductsFromDB(results, allProducts));
//        }
//
//        // Fallback keyword search
//        String[] keywords = query.toLowerCase().split("\\s+");
//        List<ProductDTO> fallback = allProducts.stream()
//                .filter(p -> {
//                    String pName = p.getName() != null ? p.getName().toLowerCase() : "";
//                    String pDesc = p.getDescription() != null ? p.getDescription().toLowerCase() : "";
//                    String pCat  = (p.getCategory() != null && p.getCategory().getName() != null)
//                            ? p.getCategory().getName().toLowerCase() : "";
//                    for (String kw : keywords) {
//                        if (kw.length() >= 2 &&
//                                (pName.contains(kw) || pDesc.contains(kw) || pCat.contains(kw))) {
//                            return true;
//                        }
//                    }
//                    return false;
//                })
//                .map(productMapper::toDTO)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(fallback);
//    }
//
//    // ── /api/predict  (AI search depuis Angular AI Assistant) ────
//    @PostMapping("/predict")
//    public ResponseEntity<List<ProductDTO>> predict(
//            @RequestBody Map<String, String> body) {
//
//        String query = body.getOrDefault("query", "");
//        if (query.trim().isEmpty()) return ResponseEntity.ok(List.of());
//
//        List<Map<String, Object>> results;
//        try {
//            // ← Augmenter top_k pour avoir plus de candidats Flask
//            results = mlService.searchTopK(query, 20);
//        } catch (Exception e) {
//            System.err.println("ML error: " + e.getMessage());
//            return ResponseEntity.ok(List.of());
//        }
//
//        List<Product> allProducts = iproductService.getAllProducts();
//        return ResponseEntity.ok(matchProductsFromDB(results, allProducts));
//    }
//
//    // ── /api/compare  (meilleur produit depuis Angular AI Assistant) ─
//    @SuppressWarnings("unchecked")
//    @PostMapping("/compare")
//    public ResponseEntity<?> compare(
//            @RequestBody Map<String, String> body) {
//
//        String query = body.getOrDefault("query", "");
//        if (query.trim().isEmpty()) return ResponseEntity.ok(Map.of());
//
//        Map<String, Object> flaskResult;
//        try {
//            flaskResult = mlService.compare(query);
//        } catch (Exception e) {
//            System.err.println("ML Service error on /compare: " + e.getMessage());
//            return ResponseEntity.ok(Map.of());
//        }
//
//        if (flaskResult == null) return ResponseEntity.ok(Map.of());
//
//        List<Product> allProducts = iproductService.getAllProducts();
//
//        // Enrichir "best" avec les vraies données DB
//        if (flaskResult.get("best") instanceof Map) {
//            Map<String, Object> best = (Map<String, Object>) flaskResult.get("best");
//            String bestName = best.get("name") != null ? best.get("name").toString().toLowerCase() : "";
//
//            allProducts.stream()
//                    .filter(p -> p.getName() != null &&
//                            (p.getName().toLowerCase().contains(bestName) ||
//                                    bestName.contains(p.getName().toLowerCase())))
//                    .findFirst()
//                    .ifPresent(p -> {
//                        best.put("id",          p.getId());
//                        best.put("imageUrl",    p.getImageUrl());
//                        best.put("stock",       p.getStock());
//                        best.put("categoryName", p.getCategory() != null
//                                ? p.getCategory().getName() : "");
//                    });
//        }
//
//        // Enrichir aussi "ranked" avec les vraies données DB
//        if (flaskResult.get("ranked") instanceof List) {
//            List<Map<String, Object>> ranked = (List<Map<String, Object>>) flaskResult.get("ranked");
//            for (Map<String, Object> item : ranked) {
//                String itemName = item.get("name") != null ? item.get("name").toString().toLowerCase() : "";
//                allProducts.stream()
//                        .filter(p -> p.getName() != null &&
//                                (p.getName().toLowerCase().contains(itemName) ||
//                                        itemName.contains(p.getName().toLowerCase())))
//                        .findFirst()
//                        .ifPresent(p -> {
//                            item.put("id",       p.getId());
//                            item.put("imageUrl", p.getImageUrl());
//                            item.put("stock",    p.getStock());
//                        });
//            }
//        }
//
//        return ResponseEntity.ok(flaskResult);
//    }
//
//    // ── Méthode utilitaire : match Flask names → vrais produits DB ─
//    private List<ProductDTO> matchProductsFromDB(
//            List<Map<String, Object>> flaskResults,
//            List<Product> allProducts) {
//
//        List<ProductDTO> matched = new ArrayList<>();
//        Set<Long> addedIds = new HashSet<>();
//        if (flaskResults == null) return matched;
//
//        for (Map<String, Object> r : flaskResults) {
//            String aiName = r.get("name") != null
//                    ? r.get("name").toString().toLowerCase() : "";
//            String aiCat  = r.get("category_name") != null
//                    ? r.get("category_name").toString().toLowerCase() : "";
//            if (aiName.isEmpty()) continue;
//
//            String[] aiWords = aiName.split("\\s+");
//
//            Product bestMatch = null;
//            int bestScore = 0;
//
//            for (Product p : allProducts) {
//                if (p.getName() == null) continue;
//                String pName = p.getName().toLowerCase();
//                String pCat  = (p.getCategory() != null && p.getCategory().getName() != null)
//                        ? p.getCategory().getName().toLowerCase() : "";
//
//                int score = 0;
//
//                // +2 par mot en commun dans le nom
//                for (String word : aiWords) {
//                    if (word.length() >= 3 && pName.contains(word)) score += 2;
//                }
//
//                // +1 si même catégorie
//                if (!aiCat.isEmpty() && pCat.contains(aiCat)) score += 1;
//
//                if (score > bestScore) {
//                    bestScore = score;
//                    bestMatch = p;
//                }
//            }
//
//            if (bestMatch != null && bestScore >= 2 && !addedIds.contains(bestMatch.getId())) {
//                matched.add(productMapper.toDTO(bestMatch));
//                addedIds.add(bestMatch.getId());
//            }
//        }
//
//        // Si toujours vide → fallback par catégorie
//        if (matched.isEmpty() && !flaskResults.isEmpty()) {
//            String aiCat = flaskResults.get(0).get("category_name") != null
//                    ? flaskResults.get(0).get("category_name").toString().toLowerCase() : "";
//            if (!aiCat.isEmpty()) {
//                allProducts.stream()
//                        .filter(p -> p.getCategory() != null
//                                && p.getCategory().getName() != null
//                                && p.getCategory().getName().toLowerCase().contains(aiCat))
//                        .map(productMapper::toDTO)
//                        .forEach(dto -> {
//                            if (!addedIds.contains(dto.getId())) {
//                                matched.add(dto);
//                                addedIds.add(dto.getId());
//                            }
//                        });
//            }
//        }
//
//        return matched;
//    }
//}