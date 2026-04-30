package tn.esprit.esprit_market.modules.store.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStockMovement;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStore;

import java.util.List;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import org.springframework.security.access.AccessDeniedException;
import tn.esprit.esprit_market.modules.order.repository.OrderItemRepository;
import tn.esprit.esprit_market.modules.store.dto.SellerAnalyticsDTO;
import tn.esprit.esprit_market.modules.order.entity.OrderItem;
import tn.esprit.esprit_market.modules.order.enums.OrderStatus;

@Service
@AllArgsConstructor
public class ServiceStore implements IserviceStore {
    private IRepositoryStore iRepositoryStore;
    private UserRepository userRepository;
    private IRepositoryStockMovement iRepositoryStockMovement;
    private OrderItemRepository orderItemRepository;
    private tn.esprit.esprit_market.modules.user.service.RateService rateService;
    private IRepositoryProduct irepositoryproduct;

    @Override
    public List<StockMovement> getLowStockAlerts(Long id) {
        return iRepositoryStockMovement.findByProduct_Store_IdAndQuantityLessThan(id, 5);
    }
    @Override
    public Store addStore(Store store, String email) {
        store.setOwner(userRepository.findByEmail(email).orElse(null));
        return iRepositoryStore.save(store);
    }

    @Override
    public Store getStoreById(Long id) {
        return iRepositoryStore.findById(id).get();
    }

    @Override
    public List<Store> getAllStores() {
        return iRepositoryStore.findAll();
    }

    @Override
    public Store updateStore(Store store, String email) {
        Store existing = iRepositoryStore.findById(store.getId())
                .orElseThrow(() -> new RuntimeException("Store not found"));
        verifyOwnership(existing, email);
        store.setOwner(existing.getOwner());
        return iRepositoryStore.save(store);
    }

    @Override
    public void deleteStore(Long id, String email) {
        Store existing = iRepositoryStore.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        verifyOwnership(existing, email);
        try {
            iRepositoryStore.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new tn.esprit.esprit_market.exceptions.UserException("Impossible de supprimer cette boutique car elle contient des produits déjà liés à des commandes ou paniers. Veuillez désactiver la boutique à la place.");
        }
    }

    private void verifyOwnership(Store store, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if ("ADMIN".equals(user.getRole().name()) || "SUPER_ADMIN".equals(user.getRole().name())) {
            return;
        }
        if (store.getOwner() == null || !store.getOwner().getEmail().equals(email)) {
            throw new AccessDeniedException("Vous n'êtes pas le propriétaire de ce store.");
        }
    }

    @Override
    public List<Store> getMyStores(String email) {
        return iRepositoryStore.findByOwnerEmail(email);
    }

    @Override
    public SellerAnalyticsDTO getStoreAnalytics(Long storeId, String email) {
        Store store = iRepositoryStore.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        verifyOwnership(store, email);

        List<tn.esprit.esprit_market.modules.store.entity.Product> products = irepositoryproduct.findByStoreId(storeId);
        int productsCount = products.size();

        // Calculate revenue & orders
        double revenue = 0;
        java.util.Set<Long> uniqueOrderIds = new java.util.HashSet<>();
        
        List<OrderItem> allItems = orderItemRepository.findAll();
        for (OrderItem item : allItems) {
            if (item.getProduct() != null && item.getProduct().getStore() != null && item.getProduct().getStore().getId().equals(storeId)) {
                if (item.getOrder() != null && item.getOrder().getStatus() != OrderStatus.CANCELLED) {
                    revenue += item.getUnitPrice() * item.getQuantity();
                    uniqueOrderIds.add(item.getOrder().getId());
                }
            }
        }

        // Avg Rating
        double avgRating = 0;
        if (store.getOwner() != null) {
            List<tn.esprit.esprit_market.modules.user.entity.Rate> rates = rateService.getRatesForUser(store.getOwner().getId());
            if (rates != null && !rates.isEmpty()) {
                double sum = rates.stream().mapToDouble(tn.esprit.esprit_market.modules.user.entity.Rate::getStar).sum();
                avgRating = sum / rates.size();
            }
        }

        return SellerAnalyticsDTO.builder()
                .revenue(revenue)
                .ordersCount(uniqueOrderIds.size())
                .productsCount(productsCount)
                .avgRating(avgRating)
                .build();
    }
}
