package tn.esprit.esprit_market.modules.store.mapper;


import org.springframework.stereotype.Component;
import tn.esprit.esprit_market.modules.administration.entity.Commission;
import tn.esprit.esprit_market.modules.administration.entity.Rule;
import tn.esprit.esprit_market.modules.marketing.entity.Advertisement;
import tn.esprit.esprit_market.modules.store.dto.StoreDTO;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.Store;


@Component
public class StoreMapper {

    public StoreDTO toDTO(Store store) {
        return StoreDTO.builder()
                .id(store.getId())
                .name(store.getName())
                .description(store.getDescription())
                .active(store.isActive())
                .createdAt(store.getCreatedAt())
                
                // ✅ Owner Info
                .ownerId(store.getOwner() != null ? store.getOwner().getId() : null)
                .ownerName(store.getOwner() != null ? store.getOwner().getName() : null)

                // ✅ Method reference + toList()
                .productIds(store.getProducts()
                        .stream()
                        .map(Product::getId)        // ✅ method reference
                        .toList())                  // ✅ toList()

                .productNames(store.getProducts()
                        .stream()
                        .map(Product::getName)      // ✅ method reference
                        .toList())                  // ✅ toList()

                // ✅ Categories
                .categoryNames(store.getCategories() != null ? store.getCategories()
                        .stream()
                        .map(tn.esprit.esprit_market.modules.store.entity.Category::getName)
                        .toList() : null)

                // ✅ Advertisements
                .advertisementIds(store.getAdvertisements()
                        .stream()
                        .map(Advertisement::getId)  // ✅ method reference
                        .toList())                  // ✅ toList()

                .advertisementTitles(store.getAdvertisements()
                        .stream()
                        .map(Advertisement::getTitle) // ✅ method reference
                        .toList())                   // ✅ toList()

                // ✅ Commissions
                .commissionIds(store.getCommissions()
                        .stream()
                        .map(Commission::getId)     // ✅ method reference
                        .toList())                  // ✅ toList()

                // ✅ Rules
                .ruleIds(store.getRules()
                        .stream()
                        .map(Rule::getId)           // ✅ method reference
                        .toList())                  // ✅ toList()

                .ruleTitles(store.getRules()
                        .stream()
                        .map(Rule::getTitle)         // ✅ method reference
                        .toList())                  // ✅ toList()

                .build();
    }
}