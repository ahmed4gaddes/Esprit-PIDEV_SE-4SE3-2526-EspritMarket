package tn.esprit.esprit_market.modules.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;

import java.util.List;

@Repository
public interface IRepositoryStockMovement extends JpaRepository<StockMovement, Long> {
    @Query("""
SELECT COALESCE(SUM(
    CASE 
        WHEN sm.type = 'IN' THEN sm.quantity
        ELSE -sm.quantity
    END
), 0)
FROM StockMovement sm 
WHERE sm.product.id = :productId
""")
    int sumQuantityByProduct(@Param("productId") Long productId);

    List<StockMovement> findByProduct_Store_IdAndQuantityLessThan(Long id, Integer threshold);


}
