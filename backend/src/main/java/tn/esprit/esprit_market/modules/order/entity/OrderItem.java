package tn.esprit.esprit_market.modules.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.service.entity.Service;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Min(value = 0, message = "Unit price must be positive")
    private double unitPrice;

    // OrderItem *..1 Order
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // OrderItem *..0..1 Product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // OrderItem *..0..1 Service
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;
}
