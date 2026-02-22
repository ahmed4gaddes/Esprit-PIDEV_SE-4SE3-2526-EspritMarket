package tn.esprit.esprit_market.modules.order.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.order.enums.PaymentMethod;

import java.util.Date;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    private String transactionRef;

    // Payment 1..1 Order
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
