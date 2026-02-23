package tn.esprit.esprit_market.modules.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

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
    private int stock;

    @Builder.Default
    private boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Product *..1 Store
    @ManyToOne
    @JsonIgnoreProperties({"products", "advertisements", "commissions", "rules", "owner"})
    @JoinColumn(name = "store_id")

    private Store store;
    // Product *..1 Category
    @ManyToOne
    @JsonIgnoreProperties({"products"})
    @JoinColumn(name = "category_id")
    private Category category;
    // Product 1..* ProductImage
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"product"})
    private List<ProductImage> images = new ArrayList<>();
    // Product 1..* StockMovement
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnoreProperties({"product"})
    private List<StockMovement> stockMovements = new ArrayList<>();
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
