package tn.esprit.esprit_market.mproduct.entites;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Column(nullable = false)
    @NotBlank
    private  int quantity;
    @NotBlank
    @Enumerated(EnumType.STRING)
    private MovementType type;
    @NotBlank
    private  Date date;
    @ManyToOne
    private Product product;
}
