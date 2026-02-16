package tn.esprit.esprit_market.mproduct.entites;

import jakarta.persistence.*;
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
    private  int quantity;
    private String type;
    private  Date date;
    private String  reason ;
    @ManyToOne
    private Product product;
}
