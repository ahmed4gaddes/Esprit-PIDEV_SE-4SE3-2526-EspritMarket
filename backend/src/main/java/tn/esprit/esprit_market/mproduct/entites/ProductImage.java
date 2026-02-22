package tn.esprit.esprit_market.mproduct.entites;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @NotBlank
    private String url;
    @NotBlank
    private String altText;
    @NotBlank
    private int orderr;
    @ManyToOne
    private Product product;
}