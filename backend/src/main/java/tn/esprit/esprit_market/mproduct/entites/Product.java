package tn.esprit.esprit_market.mproduct.entites;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @Column(nullable = false)
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Column(nullable = false)
    @NotBlank
    private double price;
    @NotBlank
    private int  stock;
    @NotBlank
    private boolean active ;
    @NotBlank
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
