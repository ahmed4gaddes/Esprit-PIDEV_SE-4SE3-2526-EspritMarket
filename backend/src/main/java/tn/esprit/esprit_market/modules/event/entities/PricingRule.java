package tn.esprit.esprit_market.modules.event.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pricing_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === Association : One PricingRule per Event ===
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    // Master toggle: allows the organizer to enable/disable dynamic pricing
    @Builder.Default
    private boolean dynamicPricingEnabled = true;

    // --- Early Bird Phase (low fill rate → discount) ---
    @Builder.Default
    private double earlyBirdDiscount = 0.20;    // 20% discount

    @Builder.Default
    private double earlyBirdThreshold = 0.30;   // applies when fill rate < 30%

    // --- High Demand Phase (medium-high fill rate → surcharge) ---
    @Builder.Default
    private double highDemandSurcharge = 0.20;   // +20%

    @Builder.Default
    private double highDemandThreshold = 0.60;   // applies when fill rate >= 60%

    // --- Last Seats Phase (very high fill rate → big surcharge) ---
    @Builder.Default
    private double lastSeatsSurcharge = 0.50;    // +50%

    @Builder.Default
    private double lastSeatsThreshold = 0.85;    // applies when fill rate >= 85%

    // --- Last Minute Phase (time-based surcharge, cumulative) ---
    @Builder.Default
    private double lastMinuteSurcharge = 0.30;   // +30%

    @Builder.Default
    private int lastMinuteHours = 24;            // applies when < 24h before event
}
