package tn.esprit.esprit_market.modules.administration.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.esprit_market.modules.store.entity.Store;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double rate;
    private double amount;

    // Commission *..1 Store
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    // Commission 1..* FinancialReport
    @OneToMany(mappedBy = "commission", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FinancialReport> financialReports = new ArrayList<>();
}
