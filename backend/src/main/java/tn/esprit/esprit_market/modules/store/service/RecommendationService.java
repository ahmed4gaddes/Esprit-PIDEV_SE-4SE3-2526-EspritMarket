package tn.esprit.esprit_market.modules.store.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.dto.ProductRecommendationDTO;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecommendationService {

    private IRepositoryProduct irepositoryproduct;


    private ProductRecommendationDTO toDTO(Product p) {
        // image est un String simple, pas une List
        String imageUrl = p.getImageUrl() != null ? p.getImageUrl() : null;

        String categoryName = (p.getCategory() != null)
                ? p.getCategory().getName()
                : null;

        return new ProductRecommendationDTO(
                p.getId(),
                p.getName(),
                p.getPrice(),
              imageUrl,
                categoryName );
    }

    // ── 1. Produits de même catégorie ──────────
    public List<ProductRecommendationDTO> getByCategory(Long productId) {
        Product product = irepositoryproduct
                .findById(productId)
                .orElseThrow();
        Long categoryId = product.getCategory().getId();
        return irepositoryproduct
                .findByCategoryIdAndIdNot(categoryId, productId)
                .stream()
                //.limit(4)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── 2. Produits prix similaire ──────────────
    public List<ProductRecommendationDTO> getBySimilarPrice(Long productId) {
        Product product = irepositoryproduct
                .findById(productId)
                .orElseThrow();

        double min = product.getPrice() * 0.7;
        double max = product.getPrice() * 1.3;
        return irepositoryproduct
                .findByPriceBetweenAndIdNot(min, max, productId)
                .stream()
                .limit(4)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── 3. Les plus vendus ──────────────────────
    // ── 3. Best sellers global ──────────────────────
    public List<ProductRecommendationDTO> getBestSellers() {
        return irepositoryproduct
                .findTopSellingProducts()
                .stream()
                .limit(6)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── 4. Best sellers même catégorie ─────────────
    public List<ProductRecommendationDTO> getBestSellersByCategory(Long productId) {
        Product product = irepositoryproduct
                .findById(productId)
                .orElseThrow();
        Long categoryId = product.getCategory().getId();

        return irepositoryproduct
                .findTopSellingProductsByCategory(categoryId)
                .stream()
                .limit(4)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── 5. Recommandation combinée ──────────────────
    public Map<String, List<ProductRecommendationDTO>> getFullRecommendations(Long productId) {
        Map<String, List<ProductRecommendationDTO>> recommendations = new HashMap<>();
        recommendations.put("sameCategory", getByCategory(productId));
        recommendations.put("similarPrice", getBySimilarPrice(productId));
        recommendations.put("bestSellers",  getBestSellersByCategory(productId)); // ← par catégorie
        return recommendations;
    }
}