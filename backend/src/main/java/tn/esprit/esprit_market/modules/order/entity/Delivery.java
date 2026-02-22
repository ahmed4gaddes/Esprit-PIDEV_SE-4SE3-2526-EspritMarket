package tn.esprit.esprit_market.modules.order.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.order.enums.DeliveryStatus;

import java.util.Date;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingNumber;
    private String carrier;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Temporal(TemporalType.DATE)
    private Date estimatedDeliveryDate;

    @Temporal(TemporalType.DATE)
    private Date actualDeliveryDate;

    private String recipientName;
    private String recipientPhone;
    private String deliveryAddress;

    @Column(columnDefinition = "TEXT")
    private String deliveryNotes;

    // Delivery 1..1 Order
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
