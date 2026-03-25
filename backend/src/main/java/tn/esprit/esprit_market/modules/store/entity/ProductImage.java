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

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String url;
    private String altText;
    @Column(name = "image_order")
    private int imageOrder;
    @ManyToOne
    @JsonIgnoreProperties({"images", "stockMovements", "store", "category"})  // ✅ évite la boucle

    @JoinColumn(name = "product_id")
    private Product product;
}
