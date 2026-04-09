package tn.esprit.esprit_market.modules.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.esprit_market.modules.order.entity.CartItem;
import tn.esprit.esprit_market.modules.order.entity.OrderItem;
import tn.esprit.esprit_market.modules.store.enums.MovementType;
import tn.esprit.esprit_market.modules.store.enums.StockStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Min(value = 0, message = "Price must be positive")
    private double price;

    @Min(value = 0, message = "Stock must be positive")
    private int stock;                          // ✅ une seule fois

    @Builder.Default
    private int stockThreshold = 5;             // ✅ une seule fois

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StockStatus stockStatus = StockStatus.IN_STOCK; // ✅ une seule fois
    @Builder.Default
    private boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JsonIgnoreProperties({"products", "advertisements", "commissions", "rules", "owner"})
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JsonIgnoreProperties({"products"})
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;



    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL , orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"product"})
    private List<StockMovement> stockMovements = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"product"})
    private List<ProductAssessment> assessments = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}