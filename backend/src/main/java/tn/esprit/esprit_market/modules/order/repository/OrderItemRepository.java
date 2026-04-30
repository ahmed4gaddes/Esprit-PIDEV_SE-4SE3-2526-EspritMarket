package tn.esprit.esprit_market.modules.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.esprit_market.modules.order.entity.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
   // List<OrderItem> findByOrderId(Long orderId);
   // Bundle intelligent
    // Récupère tous les items groupés par order
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    // Tous les product_id achetés dans la même commande que ce produit
    @Query(value = """
        SELECT oi2.product_id, COUNT(*) as freq
        FROM order_items oi1
        JOIN order_items oi2 ON oi1.order_id = oi2.order_id
        WHERE oi1.product_id = :productId
        AND oi2.product_id != :productId
        GROUP BY oi2.product_id
        ORDER BY freq DESC
        LIMIT 3
        """, nativeQuery = true)
    List<Object[]> findFrequentlyBoughtTogether(@Param("productId") Long productId);
}
