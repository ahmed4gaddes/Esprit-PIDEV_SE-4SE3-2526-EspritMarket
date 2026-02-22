package tn.esprit.esprit_market.modules.store.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Quantity is required")
    private int quantity;

    private String type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String reason;

    // StockMovement *..1 Product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
