package tn.esprit.esprit_market.modules.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DynamicPriceResponse {

    private double basePrice;           // Original price set by the organizer (e.g. 50.0)
    private double currentPrice;        // Calculated dynamic price (e.g. 60.0)
    private double discountPercent;     // If early bird: e.g. 20.0 (%)
    private double surchargePercent;    // If high demand: e.g. 20.0 (%)
    private String pricingPhase;        // EARLY_BIRD, NORMAL, HIGH_DEMAND, LAST_SEATS
    private double fillRate;            // e.g. 73.3 (%)
    private int remainingSeats;         // e.g. 8
    private double nextPriceChangeAt;   // Fill rate % at which price will change next
    private boolean isLastMinute;       // true if < lastMinuteHours before event
    private String pricingLabel;        // User-facing label e.g. "🐦 Early Bird! Save 20%"
    private boolean dynamicPricingEnabled;  // Whether dynamic pricing is active
}
