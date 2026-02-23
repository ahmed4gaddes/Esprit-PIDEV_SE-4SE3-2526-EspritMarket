package tn.esprit.esprit_market.modules.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String altText;

    @Column(name = "image_order")
    private int imageOrder;

    // ProductImage *..1 Product
    @ManyToOne
    @JsonIgnoreProperties({"images", "stockMovements", "store", "category"})
    @JoinColumn(name = "product_id")
    private Product product;
}
