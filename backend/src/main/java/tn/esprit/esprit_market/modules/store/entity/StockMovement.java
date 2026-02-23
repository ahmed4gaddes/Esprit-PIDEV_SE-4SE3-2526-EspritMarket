package tn.esprit.esprit_market.modules.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import tn.esprit.esprit_market.modules.store.enums.MovementType;

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
    @NotNull(message = "movementtype is required")
    @Enumerated(EnumType.STRING)
    private MovementType type;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String reason;

    // StockMovement *..1 Product
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"stockMovements", "images", "store", "category"})
    private Product product;
}
