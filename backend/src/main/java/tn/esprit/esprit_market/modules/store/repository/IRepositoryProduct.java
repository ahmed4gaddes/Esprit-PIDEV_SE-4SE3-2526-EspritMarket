package tn.esprit.esprit_market.modules.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.enums.StockStatus;

import java.util.List;

@Repository
public interface IRepositoryProduct extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();
    List<Product> findByStockStatus(StockStatus stockStatus);
    List<Product> findByStoreId(Long storeId);

    // IRepositoryProduct.java
    // ProductRepository.java
    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId)")
    List<Product> searchProducts(
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("categoryId") Long categoryId
    );
    // IRepositoryProduct.java
    List<Product> findByCategoryIdAndIdNot(
            Long categoryId, Long id
    );

    List<Product> findByPriceBetweenAndIdNot(
            double min, double max, Long id
    );

    List<Product> findByCategoryIdAndPriceBetweenAndIdNot(
            Long categoryId, double min, double max, Long id
    );
    // ── Best sellers global (basé sur StockMovement) ──
    @Query("SELECT p FROM Product p " +
            "JOIN StockMovement sm ON sm.product = p " +
            "WHERE sm.type = tn.esprit.esprit_market.modules.store.enums.MovementType.OUT " +
            "GROUP BY p " +
            "ORDER BY SUM(sm.quantity) ASC") // ASC car quantity OUT est négatif
    List<Product> findTopSellingProducts();

    // ── Best sellers par catégorie ──
    @Query("SELECT p FROM Product p " +
            "JOIN StockMovement sm ON sm.product = p " +
            "WHERE sm.type = tn.esprit.esprit_market.modules.store.enums.MovementType.OUT " +
            "AND p.category.id = :categoryId " +
            "GROUP BY p " +
            "ORDER BY SUM(sm.quantity) ASC")
    List<Product> findTopSellingProductsByCategory(@Param("categoryId") Long categoryId);
    //bundl
    List<Product> findByNameContainingIgnoreCase(String name);
}
