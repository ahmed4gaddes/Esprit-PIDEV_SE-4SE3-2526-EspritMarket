package tn.esprit.esprit_market.mproduct.entites;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String url;
    private String altText;
    private int orderr;
    @ManyToOne
    private Product product;
}