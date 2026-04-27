package tn.esprit.esprit_market.modules.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.event.dto.DynamicPriceResponse;
import tn.esprit.esprit_market.modules.event.dto.PricingRuleRequest;
import tn.esprit.esprit_market.modules.event.entities.Event;
import tn.esprit.esprit_market.modules.event.entities.PricingRule;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.event.repositories.PricingRuleRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import org.springframework.security.access.AccessDeniedException;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicPricingService {

    private final PricingRuleRepository pricingRuleRepository;
    private final EventRepository eventRepository;
    private final IUserService userService;

    // =====================================================================
    // Calcul du prix dynamique pour un événement
    // =====================================================================
    public DynamicPriceResponse calculateCurrentPrice(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        double basePrice = event.getTicketPrice();
        int capacity = event.getCapacity();
        int ticketsSold = event.getTickets() != null ? event.getTickets().size() : 0;
        int remainingSeats = capacity - ticketsSold;

        // Check if dynamic pricing is configured for this event
        Optional<PricingRule> optRule = pricingRuleRepository.findByEventId(eventId);

        if (optRule.isEmpty() || !optRule.get().isDynamicPricingEnabled()) {
            // No dynamic pricing → return base price
            return DynamicPriceResponse.builder()
                    .basePrice(basePrice)
                    .currentPrice(basePrice)
                    .discountPercent(0)
                    .surchargePercent(0)
                    .pricingPhase("FIXED")
                    .fillRate(capacity > 0 ? (ticketsSold * 100.0 / capacity) : 0)
                    .remainingSeats(remainingSeats)
                    .nextPriceChangeAt(0)
                    .isLastMinute(false)
                    .pricingLabel("Prix fixe")
                    .dynamicPricingEnabled(false)
                    .build();
        }

        PricingRule rule = optRule.get();
        double fillRate = capacity > 0 ? (double) ticketsSold / capacity : 0;
        double fillRatePercent = fillRate * 100.0;

        // Calculate hours until event
        long hoursUntilEvent = Long.MAX_VALUE;
        if (event.getDate() != null) {
            long diffMs = event.getDate().getTime() - new Date().getTime();
            hoursUntilEvent = diffMs / (1000 * 60 * 60);
        }
        boolean isLastMinute = hoursUntilEvent < rule.getLastMinuteHours() && hoursUntilEvent >= 0;

        // Determine pricing phase
        String phase;
        double currentPrice;
        double discountPercent = 0;
        double surchargePercent = 0;
        double nextChangeAt;
        String label;

        if (fillRate < rule.getEarlyBirdThreshold()) {
            // === EARLY BIRD ===
            phase = "EARLY_BIRD";
            discountPercent = rule.getEarlyBirdDiscount() * 100;
            currentPrice = basePrice * (1 - rule.getEarlyBirdDiscount());
            nextChangeAt = rule.getEarlyBirdThreshold() * 100;
            label = String.format("🐦 Early Bird ! Économisez %.0f%%", discountPercent);

        } else if (fillRate < rule.getHighDemandThreshold()) {
            // === NORMAL ===
            phase = "NORMAL";
            currentPrice = basePrice;
            nextChangeAt = rule.getHighDemandThreshold() * 100;
            label = String.format("💡 Tarif normal — %d places restantes", remainingSeats);

        } else if (fillRate < rule.getLastSeatsThreshold()) {
            // === HIGH DEMAND ===
            phase = "HIGH_DEMAND";
            surchargePercent = rule.getHighDemandSurcharge() * 100;
            currentPrice = basePrice * (1 + rule.getHighDemandSurcharge());
            nextChangeAt = rule.getLastSeatsThreshold() * 100;
            label = String.format("📈 Forte demande ! %d places restantes", remainingSeats);

        } else {
            // === LAST SEATS ===
            phase = "LAST_SEATS";
            surchargePercent = rule.getLastSeatsSurcharge() * 100;
            currentPrice = basePrice * (1 + rule.getLastSeatsSurcharge());
            nextChangeAt = 100;
            label = String.format("🔥 Dernières %d places !", remainingSeats);
        }

        // Apply last minute surcharge (cumulative)
        if (isLastMinute) {
            double lastMinuteExtra = rule.getLastMinuteSurcharge() * 100;
            surchargePercent += lastMinuteExtra;
            currentPrice = currentPrice * (1 + rule.getLastMinuteSurcharge());
            phase = phase + "_LAST_MINUTE";
            label = "⏰ " + label + String.format(" — Last Minute +%.0f%%", lastMinuteExtra);
        }

        // Round to 2 decimal places
        currentPrice = Math.round(currentPrice * 100.0) / 100.0;

        log.info("Dynamic Pricing — Event [{}]: base={}, current={}, phase={}, fillRate={}%",
                eventId, basePrice, currentPrice, phase, fillRatePercent);

        return DynamicPriceResponse.builder()
                .basePrice(basePrice)
                .currentPrice(currentPrice)
                .discountPercent(discountPercent)
                .surchargePercent(surchargePercent)
                .pricingPhase(phase)
                .fillRate(Math.round(fillRatePercent * 10.0) / 10.0)
                .remainingSeats(remainingSeats)
                .nextPriceChangeAt(nextChangeAt)
                .isLastMinute(isLastMinute)
                .pricingLabel(label)
                .dynamicPricingEnabled(true)
                .build();
    }

    // =====================================================================
    // Crée ou configure une PricingRule pour un événement
    // =====================================================================
    @Transactional
    public PricingRule createPricingRule(Long eventId, PricingRuleRequest request, String userEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        verifyOwnership(event, userEmail);

        // Check if a rule already exists
        if (pricingRuleRepository.existsByEventId(eventId)) {
            throw new IllegalStateException("A pricing rule already exists for event " + eventId
                    + ". Use PUT to update it.");
        }

        PricingRule rule = PricingRule.builder()
                .event(event)
                .dynamicPricingEnabled(request.isDynamicPricingEnabled())
                .earlyBirdDiscount(request.getEarlyBirdDiscount())
                .earlyBirdThreshold(request.getEarlyBirdThreshold())
                .highDemandSurcharge(request.getHighDemandSurcharge())
                .highDemandThreshold(request.getHighDemandThreshold())
                .lastSeatsSurcharge(request.getLastSeatsSurcharge())
                .lastSeatsThreshold(request.getLastSeatsThreshold())
                .lastMinuteSurcharge(request.getLastMinuteSurcharge())
                .lastMinuteHours(request.getLastMinuteHours())
                .build();

        PricingRule saved = pricingRuleRepository.save(rule);
        log.info("Dynamic Pricing Rule CREATED for Event [{}] by user [{}]", eventId, userEmail);
        return saved;
    }

    // =====================================================================
    // Met à jour la PricingRule d'un événement
    // =====================================================================
    @Transactional
    public PricingRule updatePricingRule(Long eventId, PricingRuleRequest request, String userEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        verifyOwnership(event, userEmail);

        PricingRule rule = pricingRuleRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No pricing rule found for event " + eventId));

        rule.setDynamicPricingEnabled(request.isDynamicPricingEnabled());
        rule.setEarlyBirdDiscount(request.getEarlyBirdDiscount());
        rule.setEarlyBirdThreshold(request.getEarlyBirdThreshold());
        rule.setHighDemandSurcharge(request.getHighDemandSurcharge());
        rule.setHighDemandThreshold(request.getHighDemandThreshold());
        rule.setLastSeatsSurcharge(request.getLastSeatsSurcharge());
        rule.setLastSeatsThreshold(request.getLastSeatsThreshold());
        rule.setLastMinuteSurcharge(request.getLastMinuteSurcharge());
        rule.setLastMinuteHours(request.getLastMinuteHours());

        PricingRule updated = pricingRuleRepository.save(rule);
        log.info("Dynamic Pricing Rule UPDATED for Event [{}] by user [{}]", eventId, userEmail);
        return updated;
    }

    // =====================================================================
    // Récupère la PricingRule d'un événement
    // =====================================================================
    public PricingRule getPricingRule(Long eventId) {
        return pricingRuleRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No pricing rule found for event " + eventId));
    }

    // =====================================================================
    // Supprime la PricingRule d'un événement (retour au prix fixe)
    // =====================================================================
    @Transactional
    public void deletePricingRule(Long eventId, String userEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        verifyOwnership(event, userEmail);

        if (!pricingRuleRepository.existsByEventId(eventId)) {
            throw new ResourceNotFoundException("No pricing rule found for event " + eventId);
        }

        pricingRuleRepository.deleteByEventId(eventId);
        log.info("Dynamic Pricing Rule DELETED for Event [{}] by user [{}]", eventId, userEmail);
    }

    // =====================================================================
    // Ownership check (same logic as EventService)
    // =====================================================================
    private void verifyOwnership(Event event, String userEmail) {
        User currentUser = userService.getUserByEmail(userEmail);
        // Admin can do anything
        if ("ADMIN".equals(currentUser.getRole().name())) {
            return;
        }
        // Check if the current user is the organizer
        if (event.getOrganizer() == null || !event.getOrganizer().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier le pricing de cet événement.");
        }
    }
}
