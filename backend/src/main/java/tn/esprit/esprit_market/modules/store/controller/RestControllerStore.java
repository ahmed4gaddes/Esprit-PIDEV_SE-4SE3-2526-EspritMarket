package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.dto.StoreDTO;
import tn.esprit.esprit_market.modules.store.entity.StockMovement;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.mapper.StoreMapper;
import tn.esprit.esprit_market.modules.store.service.IserviceStore;

import java.util.List;
import java.util.stream.Collectors;


@RequestMapping("Store")
@RestController
@AllArgsConstructor
public class RestControllerStore {
    private IserviceStore iserviceStore;
    private StoreMapper storeMapper;
    @GetMapping("/{id}/lowstock")
    public List<StockMovement> getLowStock(@PathVariable Long id) {
        return iserviceStore.getLowStockAlerts(id);
    }
@PostMapping("addstore")
public StoreDTO addStore(@RequestBody Store store) {
    org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Store savedStore = iserviceStore.addStore(store, email);
    return storeMapper.toDTO(savedStore);
}

    @GetMapping("get/{id}")
    public StoreDTO getStoreById(@PathVariable Long id) {
        Store store = iserviceStore.getStoreById(id);
        return storeMapper.toDTO(store);
    }


    @GetMapping("getall")
    public List<StoreDTO> getAllStores() {
        return iserviceStore.getAllStores()
                .stream()
                .map(storeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ GET specifically for the logged-in User
    @GetMapping("my-stores")
    public List<StoreDTO> getMyStores() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return iserviceStore.getMyStores(email)
                .stream()
                .map(storeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/analytics")
    public ResponseEntity<tn.esprit.esprit_market.modules.store.dto.SellerAnalyticsDTO> getStoreAnalytics(@PathVariable Long id) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(iserviceStore.getStoreAnalytics(id, email));
    }

   @PutMapping("update")
    public StoreDTO updateStore(@RequestBody Store store) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Store updatedStore = iserviceStore.updateStore(store, email);
        return storeMapper.toDTO(updatedStore);
    }

    @DeleteMapping("delete/{id}")
    public void deleteStore(@PathVariable Long id) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        iserviceStore.deleteStore(id, email);
    }

}
