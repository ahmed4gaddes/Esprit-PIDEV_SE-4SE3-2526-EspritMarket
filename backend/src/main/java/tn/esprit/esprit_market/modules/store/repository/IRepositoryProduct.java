package tn.esprit.esprit_market.modules.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.enums.StockStatus;

import java.util.List;

@Repository
public interface IRepositoryProduct extends JpaRepository<Product, Long> {
    // Long id(Long id);
    List<Product> findByActiveTrue();
    List<Product> findByStockStatus(StockStatus stockStatus);

    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> searchProducts(@org.springframework.data.repository.query.Param("name") String name,
                                 @org.springframework.data.repository.query.Param("categoryId") Long categoryId,
                                 @org.springframework.data.repository.query.Param("minPrice") Double minPrice,
                                 @org.springframework.data.repository.query.Param("maxPrice") Double maxPrice);

}
