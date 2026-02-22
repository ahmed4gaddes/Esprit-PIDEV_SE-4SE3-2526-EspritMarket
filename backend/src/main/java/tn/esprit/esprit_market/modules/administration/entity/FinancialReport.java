package tn.esprit.esprit_market.modules.administration.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "financial_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double revenue;
    private double commissionAmount;
    private double net;

    @Temporal(TemporalType.DATE)
    private Date date;

    // FinancialReport *..1 Commission
    @ManyToOne
    @JoinColumn(name = "commission_id")
    private Commission commission;
}
