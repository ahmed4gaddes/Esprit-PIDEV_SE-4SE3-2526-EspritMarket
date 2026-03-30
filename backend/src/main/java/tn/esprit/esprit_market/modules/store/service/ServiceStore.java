package tn.esprit.esprit_market.modules.store.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStore;

import java.util.List;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import org.springframework.security.access.AccessDeniedException;

@Service
@AllArgsConstructor
public class ServiceStore implements IserviceStore {
    private IRepositoryStore iRepositoryStore;
    private UserRepository userRepository;
    
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
}
