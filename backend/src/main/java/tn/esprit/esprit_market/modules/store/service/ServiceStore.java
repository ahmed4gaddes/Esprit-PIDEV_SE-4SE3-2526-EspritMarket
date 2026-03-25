package tn.esprit.esprit_market.modules.store.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStore;

import java.util.List;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

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
    public Store updateStore( Store store) {
        return iRepositoryStore.save(store);
    }

    @Override
    public void deleteStore(Long id) {
        iRepositoryStore.deleteById(id);
    }

    @Override
    public List<Store> getMyStores(String email) {
        return iRepositoryStore.findByOwnerEmail(email);
    }
}
