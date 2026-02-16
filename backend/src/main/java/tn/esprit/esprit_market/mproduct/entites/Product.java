package tn.esprit.esprit_market.mproduct.entites;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double price;
    private int  stock;
    private boolean active ;
    private Date createdAt;

    @ManyToOne
    private Store store;
    @ManyToOne
    private Category category;
    @OneToMany ( mappedBy = "product")
    private Set<ProductImage> images;
    @OneToMany ( mappedBy = "product")
    private Set<StockMovement> stockMovements;

}
